

package org.springframework.context.annotation;

import org.junit.Test;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.tests.sample.beans.TestBean;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Tests semantics of declaring {@link BeanFactoryPostProcessor}-returning @Bean
 * methods, specifically as regards static @Bean methods and the avoidance of
 * container lifecycle issues when BFPPs are in the mix.

 * @since 3.1
 */
public class ConfigurationClassAndBFPPTests {

	@Test
	public void autowiringFailsWithBFPPAsInstanceMethod() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(TestBeanConfig.class, AutowiredConfigWithBFPPAsInstanceMethod.class);
		ctx.refresh();
		// instance method BFPP interferes with lifecycle -> autowiring fails!
		// WARN-level logging should have been issued about returning BFPP from non-static @Bean method
		assertThat(ctx.getBean(AutowiredConfigWithBFPPAsInstanceMethod.class).autowiredTestBean, nullValue());
	}

	@Test
	public void autowiringSucceedsWithBFPPAsStaticMethod() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(TestBeanConfig.class, AutowiredConfigWithBFPPAsStaticMethod.class);
		ctx.refresh();
		// static method BFPP does not interfere with lifecycle -> autowiring succeeds
		assertThat(ctx.getBean(AutowiredConfigWithBFPPAsStaticMethod.class).autowiredTestBean, notNullValue());
	}


	@Configuration
	static class TestBeanConfig {
		@Bean
		public TestBean testBean() {
			return new TestBean();
		}
	}


	@Configuration
	static class AutowiredConfigWithBFPPAsInstanceMethod {
		@Autowired TestBean autowiredTestBean;

		@Bean
		public BeanFactoryPostProcessor bfpp() {
			return new BeanFactoryPostProcessor() {
				@Override
				public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
					// no-op
				}
			};
		}
	}


	@Configuration
	static class AutowiredConfigWithBFPPAsStaticMethod {
		@Autowired TestBean autowiredTestBean;

		@Bean
		public static final BeanFactoryPostProcessor bfpp() {
			return new BeanFactoryPostProcessor() {
				@Override
				public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
					// no-op
				}
			};
		}
	}


	@Test
	public void staticBeanMethodsDoNotRespectScoping() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(ConfigWithStaticBeanMethod.class);
		ctx.refresh();
		assertThat(ConfigWithStaticBeanMethod.testBean(), not(sameInstance(ConfigWithStaticBeanMethod.testBean())));
	}


	@Configuration
	static class ConfigWithStaticBeanMethod {
		@Bean
		public static TestBean testBean() {
			return new TestBean("foo");
		}
	}
}
