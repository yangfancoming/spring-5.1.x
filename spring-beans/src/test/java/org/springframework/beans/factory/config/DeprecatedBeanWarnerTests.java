

package org.springframework.beans.factory.config;

import org.junit.Test;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

import static org.junit.Assert.*;


public class DeprecatedBeanWarnerTests {

	private String beanName;

	private BeanDefinition beanDefinition;


	@Test
	@SuppressWarnings("deprecation")
	public void postProcess() {
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		BeanDefinition def = new RootBeanDefinition(MyDeprecatedBean.class);
		String beanName = "deprecated";
		beanFactory.registerBeanDefinition(beanName, def);

		DeprecatedBeanWarner warner = new MyDeprecatedBeanWarner();
		warner.postProcessBeanFactory(beanFactory);
		assertEquals(beanName, this.beanName);
		assertEquals(def, this.beanDefinition);
	}


	private class MyDeprecatedBeanWarner extends DeprecatedBeanWarner {

		@Override
		protected void logDeprecatedBean(String beanName, Class<?> beanType, BeanDefinition beanDefinition) {
			DeprecatedBeanWarnerTests.this.beanName = beanName;
			DeprecatedBeanWarnerTests.this.beanDefinition = beanDefinition;
		}
	}

}
