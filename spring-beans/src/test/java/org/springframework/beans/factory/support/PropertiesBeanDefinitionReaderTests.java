

package org.springframework.beans.factory.support;

import org.junit.Test;

import org.springframework.core.io.ClassPathResource;
import org.springframework.tests.sample.beans.TestBean;

import static org.junit.Assert.*;


public class PropertiesBeanDefinitionReaderTests {

	private final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

	private final PropertiesBeanDefinitionReader reader = new PropertiesBeanDefinitionReader(this.beanFactory);


	@Test
	public void withSimpleConstructorArg() {
		this.reader.loadBeanDefinitions(new ClassPathResource("simpleConstructorArg.properties", getClass()));
		TestBean bean = (TestBean) this.beanFactory.getBean("testBean");
		assertEquals("Rob Harrop", bean.getName());
	}

	@Test
	public void withConstructorArgRef() {
		this.reader.loadBeanDefinitions(new ClassPathResource("refConstructorArg.properties", getClass()));
		TestBean rob = (TestBean) this.beanFactory.getBean("rob");
		TestBean sally = (TestBean) this.beanFactory.getBean("sally");
		assertEquals(sally, rob.getSpouse());
	}

	@Test
	public void withMultipleConstructorsArgs() {
		this.reader.loadBeanDefinitions(new ClassPathResource("multiConstructorArgs.properties", getClass()));
		TestBean bean = (TestBean) this.beanFactory.getBean("testBean");
		assertEquals("Rob Harrop", bean.getName());
		assertEquals(23, bean.getAge());
	}

}
