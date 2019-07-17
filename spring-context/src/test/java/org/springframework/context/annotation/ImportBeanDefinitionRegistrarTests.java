

package org.springframework.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

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
	}


	@Sample
	@Configuration
	static class Config {
	}


	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	@Import(SampleRegistrar.class)
	public @interface Sample {
	}


	private static class SampleRegistrar implements ImportBeanDefinitionRegistrar,
			BeanClassLoaderAware, ResourceLoaderAware, BeanFactoryAware, EnvironmentAware {

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
		public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
				BeanDefinitionRegistry registry) {
		}
	}

}
