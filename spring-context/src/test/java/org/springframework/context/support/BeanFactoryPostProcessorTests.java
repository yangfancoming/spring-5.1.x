

package org.springframework.context.support;

import org.junit.Test;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.util.Assert;

import static org.junit.Assert.*;

/**
 * Tests the interaction between {@link ApplicationContext} implementations and any registered {@link BeanFactoryPostProcessor} implementations.
 * Specifically  {@link StaticApplicationContext} is used for the tests, but what's represented  here is any {@link AbstractApplicationContext} implementation.
 * @since 02.10.2003
 */
public class BeanFactoryPostProcessorTests {

	StaticApplicationContext ac = new StaticApplicationContext();

	// 测试 使用向容器中注册 后置处理器的方式，调用接口实现方法
	@Test
	public void testRegisteredBeanFactoryPostProcessor() {
		TestBeanFactoryPostProcessor bfpp = new TestBeanFactoryPostProcessor();
		// 在容器中注册 BeanFactoryPostProcessor 后置处理器
		ac.addBeanFactoryPostProcessor(bfpp);
		assertFalse(bfpp.wasCalled); // 容器刷新前 不会调用
		ac.refresh(); // 容器刷新会调用 BeanFactoryPostProcessor 接口的实现方法
		assertTrue(bfpp.wasCalled); // 容器刷新后调用了 BeanFactoryPostProcessor接口的实现方法
	}

	// 测试 使用向容器中注册bean定义的方式， 使用后置处理器的方式，调用接口实现方法
	@Test
	public void testDefinedBeanFactoryPostProcessor() {
		ac.registerSingleton("bfpp", TestBeanFactoryPostProcessor.class);
		TestBeanFactoryPostProcessor bfpp = (TestBeanFactoryPostProcessor) ac.getBean("bfpp");
		assertFalse(bfpp.wasCalled); // 容器刷新前 不会调用
		ac.refresh();
		assertTrue(bfpp.wasCalled);
	}

	@Test
	public void testMultipleDefinedBeanFactoryPostProcessors() {
		MutablePropertyValues pvs1 = new MutablePropertyValues();
		pvs1.add("initValue", "${key}");
		ac.registerSingleton("bfpp1", TestBeanFactoryPostProcessor.class, pvs1);

		MutablePropertyValues pvs2 = new MutablePropertyValues();
		pvs2.add("properties", "key=value");
		ac.registerSingleton("bfpp2", PropertyPlaceholderConfigurer.class, pvs2);

		ac.refresh();
		TestBeanFactoryPostProcessor bfpp = (TestBeanFactoryPostProcessor) ac.getBean("bfpp1");
		assertEquals("value", bfpp.initValue);
		assertTrue(bfpp.wasCalled);
	}

	// 测试 BeanFactoryPostProcessor 接口
	@Test
	public void testBeanFactoryPostProcessorNotExecutedByBeanFactory() {
		DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
		bf.registerBeanDefinition("tb1", new RootBeanDefinition(TestBean.class));
		bf.registerBeanDefinition("tb2", new RootBeanDefinition(TestBean.class));
		bf.registerBeanDefinition("bfpp", new RootBeanDefinition(TestBeanFactoryPostProcessor.class));
		TestBeanFactoryPostProcessor bfpp = (TestBeanFactoryPostProcessor) bf.getBean("bfpp");
		assertFalse(bfpp.wasCalled);
	}


	// 测试 BeanDefinitionRegistryPostProcessor 接口
	@Test
	public void testBeanDefinitionRegistryPostProcessor() {
		PrioritizedBeanDefinitionRegistryPostProcessor bfpp1 = new PrioritizedBeanDefinitionRegistryPostProcessor();
		ac.addBeanFactoryPostProcessor(bfpp1);
		TestBeanDefinitionRegistryPostProcessor bfpp2 = new TestBeanDefinitionRegistryPostProcessor();
		ac.addBeanFactoryPostProcessor(bfpp2);
		assertFalse(bfpp2.wasCalled);
		ac.refresh();
		// 测试 BeanDefinitionRegistryPostProcessor 接口被调用
		assertTrue(bfpp2.wasCalled);
		// 测试 BeanFactoryPostProcessor 接口被调用
		assertTrue(ac.getBean("bfpp1", TestBeanFactoryPostProcessor.class).wasCalled);
		assertTrue(ac.getBean("bfpp2", TestBeanFactoryPostProcessor.class).wasCalled);
	}

	@Test
	public void testBeanDefinitionRegistryPostProcessorRegisteringAnother() {
		ac.registerSingleton("tb1", TestBean.class);
		ac.registerSingleton("tb2", TestBean.class);
		ac.registerBeanDefinition("bdrpp2", new RootBeanDefinition(OuterBeanDefinitionRegistryPostProcessor.class));
		ac.refresh();
		assertTrue(ac.getBean("bfpp1", TestBeanFactoryPostProcessor.class).wasCalled);
		assertTrue(ac.getBean("bfpp2", TestBeanFactoryPostProcessor.class).wasCalled);
	}

	@Test
	public void testPrioritizedBeanDefinitionRegistryPostProcessorRegisteringAnother() {
		ac.registerSingleton("tb1", TestBean.class);
		ac.registerSingleton("tb2", TestBean.class);
		ac.registerBeanDefinition("bdrpp2", new RootBeanDefinition(PrioritizedOuterBeanDefinitionRegistryPostProcessor.class));
		ac.refresh();
		assertTrue(ac.getBean("bfpp1", TestBeanFactoryPostProcessor.class).wasCalled);
		assertTrue(ac.getBean("bfpp2", TestBeanFactoryPostProcessor.class).wasCalled);
	}

	@Test
	public void testBeanFactoryPostProcessorAsApplicationListener() {
		ac.registerBeanDefinition("bfpp", new RootBeanDefinition(ListeningBeanFactoryPostProcessor.class));
		ac.refresh();
		assertTrue(ac.getBean(ListeningBeanFactoryPostProcessor.class).received instanceof ContextRefreshedEvent);
	}

	@Test
	public void testBeanFactoryPostProcessorWithInnerBeanAsApplicationListener() {
		RootBeanDefinition rbd = new RootBeanDefinition(NestingBeanFactoryPostProcessor.class);
		rbd.getPropertyValues().add("listeningBean", new RootBeanDefinition(ListeningBean.class));
		ac.registerBeanDefinition("bfpp", rbd);
		ac.refresh();
		assertTrue(ac.getBean(NestingBeanFactoryPostProcessor.class).getListeningBean().received instanceof ContextRefreshedEvent);
	}


	public static class TestBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

		public String initValue;

		public boolean wasCalled = false;

		public void setInitValue(String initValue) {
			this.initValue = initValue;
		}

		@Override
		public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
			wasCalled = true;
		}
	}


	public static class PrioritizedBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, Ordered {

		@Override
		public int getOrder() {
			return Ordered.HIGHEST_PRECEDENCE;
		}

		@Override
		public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
			registry.registerBeanDefinition("bfpp1", new RootBeanDefinition(TestBeanFactoryPostProcessor.class));
		}

		@Override
		public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		}
	}


	public static class TestBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

		public boolean wasCalled;

		@Override
		public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
			assertTrue(registry.containsBeanDefinition("bfpp1"));
			registry.registerBeanDefinition("bfpp2", new RootBeanDefinition(TestBeanFactoryPostProcessor.class));
		}

		@Override
		public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
			this.wasCalled = true;
		}
	}


	public static class OuterBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

		@Override
		public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
			registry.registerBeanDefinition("anotherpp", new RootBeanDefinition(TestBeanDefinitionRegistryPostProcessor.class));
			registry.registerBeanDefinition("ppp", new RootBeanDefinition(PrioritizedBeanDefinitionRegistryPostProcessor.class));
		}

		@Override
		public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		}
	}


	public static class PrioritizedOuterBeanDefinitionRegistryPostProcessor extends OuterBeanDefinitionRegistryPostProcessor implements PriorityOrdered {
		@Override
		public int getOrder() {
			return HIGHEST_PRECEDENCE;
		}
	}

	public static class ListeningBeanFactoryPostProcessor implements BeanFactoryPostProcessor, ApplicationListener {

		public ApplicationEvent received;

		@Override
		public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		}

		@Override
		public void onApplicationEvent(ApplicationEvent event) {
			Assert.state(this.received == null, "Just one ContextRefreshedEvent expected");
			this.received = event;
		}
	}

	public static class ListeningBean implements ApplicationListener {

		public ApplicationEvent received;

		@Override
		public void onApplicationEvent(ApplicationEvent event) {
			Assert.state(this.received == null, "Just one ContextRefreshedEvent expected");
			this.received = event;
		}
	}

	public static class NestingBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

		private ListeningBean listeningBean;

		public void setListeningBean(ListeningBean listeningBean) {
			this.listeningBean = listeningBean;
		}

		public ListeningBean getListeningBean() {
			return listeningBean;
		}

		@Override
		public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		}
	}

}
