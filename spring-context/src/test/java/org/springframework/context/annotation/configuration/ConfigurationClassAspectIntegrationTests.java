

package org.springframework.context.annotation.configuration;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.junit.Test;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.tests.sample.beans.TestBean;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * System tests covering use of AspectJ {@link Aspect}s in conjunction with {@link Configuration} classes.
 * {@link Bean} methods may return aspects, or Configuration classes may themselves be annotated with Aspect.
 * In the latter case, advice methods are declared inline within the Configuration class.  This makes for a
 * particularly convenient syntax requiring no extra artifact for the aspect.
 *
 * <p>Currently it is assumed that the user is bootstrapping Configuration class processing via XML (using
 * annotation-config or component-scan), and thus will also use {@code <aop:aspectj-autoproxy/>} to enable
 * processing of the Aspect annotation.


 */
public class ConfigurationClassAspectIntegrationTests {

	@Test
	public void aspectAnnotatedConfiguration() {
		assertAdviceWasApplied(AspectConfig.class);
	}

	@Test
	public void configurationIncludesAspect() {
		assertAdviceWasApplied(ConfigurationWithAspect.class);
	}

	private void assertAdviceWasApplied(Class<?> configClass) {
		DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
		new XmlBeanDefinitionReader(factory).loadBeanDefinitions(
				new ClassPathResource("aspectj-autoproxy-config.xml", ConfigurationClassAspectIntegrationTests.class));
		GenericApplicationContext ctx = new GenericApplicationContext(factory);
		ctx.addBeanFactoryPostProcessor(new ConfigurationClassPostProcessor());
		ctx.registerBeanDefinition("config", new RootBeanDefinition(configClass));
		ctx.refresh();

		TestBean testBean = ctx.getBean("testBean", TestBean.class);
		assertThat(testBean.getName(), equalTo("name"));
		testBean.absquatulate();
		assertThat(testBean.getName(), equalTo("advisedName"));
	}

	@Test
	public void withInnerClassAndLambdaExpression() {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(Application.class, CountingAspect.class);
		ctx.getBeansOfType(Runnable.class).forEach((k, v) -> v.run());

		// TODO: returns just 1 as of AspectJ 1.9 beta 3, not detecting the applicable lambda expression anymore
		// assertEquals(2, ctx.getBean(CountingAspect.class).count);
	}


	@Aspect
	@Configuration
	static class AspectConfig {

		@Bean
		public TestBean testBean() {
			return new TestBean("name");
		}

		@Before("execution(* org.springframework.tests.sample.beans.TestBean.absquatulate(..)) && target(testBean)")
		public void touchBean(TestBean testBean) {
			testBean.setName("advisedName");
		}
	}


	@Configuration
	static class ConfigurationWithAspect {

		@Bean
		public TestBean testBean() {
			return new TestBean("name");
		}

		@Bean
		public NameChangingAspect nameChangingAspect() {
			return new NameChangingAspect();
		}
	}


	@Aspect
	static class NameChangingAspect {

		@Before("execution(* org.springframework.tests.sample.beans.TestBean.absquatulate(..)) && target(testBean)")
		public void touchBean(TestBean testBean) {
			testBean.setName("advisedName");
		}
	}



	@Configuration
	@EnableAspectJAutoProxy
	public static class Application {

		@Bean
		Runnable fromInnerClass() {
			return new Runnable() {
				@Override
				public void run() {
				}
			};
		}

		@Bean
		Runnable fromLambdaExpression() {
			return () -> {
			};
		}
	}


	@Aspect
	public static class CountingAspect {

		public int count = 0;

		@After("execution(* java.lang.Runnable.*(..))")
		public void after(JoinPoint joinPoint) {
			count++;
		}
	}

}
