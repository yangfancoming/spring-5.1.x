

package org.springframework.context.annotation;

import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.MessageSource;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Integration tests for {@link ImportBeanDefinitionRegistrar}.
 */
public class ImportBeanDefinitionRegistrarTests {

	@Test
	public void shouldInvokeAwareMethodsInImportBeanDefinitionRegistrar() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
		context.getBean(MessageSource.class);
		assertThat(SampleRegistrar.beanFactory, is(context.getBeanFactory()));
		assertThat(SampleRegistrar.classLoader, is(context.getBeanFactory().getBeanClassLoader()));
		assertThat(SampleRegistrar.resourceLoader, is(notNullValue()));
		assertThat(SampleRegistrar.environment, is(context.getEnvironment()));

		/**
		 * 由于 Goat.class 就是一个简单 @Component 会被注册到容器中
		 * 但 SampleRegistrar.class 实现了 ImportBeanDefinitionRegistrar 接口，不会被注册到容器中
		 * 如果 去掉实现 ImportBeanDefinitionRegistrar 接口，则可以注册到容器中
		*/
		assertNotNull(context.getBean(Goat.class));
		SampleRegistrar bean = context.getBean(SampleRegistrar.class);
		System.out.println(bean);
	}

	/**
	 * @Configuration 配置类Config 会解析到其上的@Sample注解上的@Import({SampleRegistrar.class}) 注解 和 @Test1 注解
	 * 而 @Test1 注解又包含了@Import({Goat.class})注解，因此 SampleRegistrar.class 和 Goat.class  都被会扫描到容器中
	*/
	@Sample
	@Configuration
	static class Config {
	}

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	@Import({SampleRegistrar.class})
	@Test1
	public @interface Sample {
	}

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	@Import({Goat.class})
	public @interface Test1 {

	}

	@Component
	private static class Goat {
	}

	private static class SampleRegistrar implements ImportBeanDefinitionRegistrar,BeanClassLoaderAware, ResourceLoaderAware, BeanFactoryAware, EnvironmentAware {
		static ClassLoader classLoader;
		static ResourceLoader resourceLoader;
		static BeanFactory beanFactory;
		static Environment environment;

		@Override
		public void setBeanClassLoader(ClassLoader classLoader) {
			SampleRegistrar.classLoader = classLoader;
		}

		@Override
		public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
			SampleRegistrar.beanFactory = beanFactory;
		}

		@Override
		public void setResourceLoader(ResourceLoader resourceLoader) {
			SampleRegistrar.resourceLoader = resourceLoader;
		}

		@Override
		public void setEnvironment(Environment environment) {
			SampleRegistrar.environment = environment;
		}

		@Override
		public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,BeanDefinitionRegistry registry) {
			System.out.println("mark registerBeanDefinitions");
		}
	}
}
