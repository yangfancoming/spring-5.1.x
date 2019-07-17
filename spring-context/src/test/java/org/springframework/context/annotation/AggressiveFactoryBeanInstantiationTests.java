

package org.springframework.context.annotation;

import org.junit.Test;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;


public class AggressiveFactoryBeanInstantiationTests {

	@Test
	public void directlyRegisteredFactoryBean() {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			context.register(SimpleFactoryBean.class);
			context.addBeanFactoryPostProcessor((factory) -> {
				BeanFactoryUtils.beanNamesForTypeIncludingAncestors(factory, String.class);
			});
			context.refresh();
		}
	}

	@Test
	public void beanMethodFactoryBean() {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			context.register(BeanMethodConfiguration.class);
			context.addBeanFactoryPostProcessor((factory) -> {
				BeanFactoryUtils.beanNamesForTypeIncludingAncestors(factory, String.class);
			});
			context.refresh();
		}
	}


	@Configuration
	static class BeanMethodConfiguration {

		@Bean
		public SimpleFactoryBean simpleFactoryBean(ApplicationContext applicationContext) {
			return new SimpleFactoryBean(applicationContext);
		}
	}


	static class SimpleFactoryBean implements FactoryBean<Object> {

		public SimpleFactoryBean(ApplicationContext applicationContext) {
		}

		@Override
		public Object getObject() {
			return new Object();
		}

		@Override
		public Class<?> getObjectType() {
			return Object.class;
		}
	}

}
