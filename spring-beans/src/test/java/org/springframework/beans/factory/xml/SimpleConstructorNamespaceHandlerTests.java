

package org.springframework.beans.factory.xml;

import org.junit.Test;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.tests.sample.beans.DummyBean;
import org.springframework.tests.sample.beans.TestBean;

import static org.junit.Assert.*;


public class SimpleConstructorNamespaceHandlerTests {

	@Test
	public void simpleValue() throws Exception {
		DefaultListableBeanFactory beanFactory = createFactory("simpleConstructorNamespaceHandlerTests.xml");
		String name = "simple";
		//		beanFactory.getBean("simple1", DummyBean.class);
		DummyBean nameValue = beanFactory.getBean(name, DummyBean.class);
		assertEquals("simple", nameValue.getValue());
	}

	@Test
	public void simpleRef() throws Exception {
		DefaultListableBeanFactory beanFactory = createFactory("simpleConstructorNamespaceHandlerTests.xml");
		String name = "simple-ref";
		//		beanFactory.getBean("name-value1", TestBean.class);
		DummyBean nameValue = beanFactory.getBean(name, DummyBean.class);
		assertEquals(beanFactory.getBean("name"), nameValue.getValue());
	}

	@Test
	public void nameValue() throws Exception {
		DefaultListableBeanFactory beanFactory = createFactory("simpleConstructorNamespaceHandlerTests.xml");
		String name = "name-value";
		//		beanFactory.getBean("name-value1", TestBean.class);
		TestBean nameValue = beanFactory.getBean(name, TestBean.class);
		assertEquals(name, nameValue.getName());
		assertEquals(10, nameValue.getAge());
	}

	@Test
	public void nameRef() throws Exception {
		DefaultListableBeanFactory beanFactory = createFactory("simpleConstructorNamespaceHandlerTests.xml");
		TestBean nameValue = beanFactory.getBean("name-value", TestBean.class);
		DummyBean nameRef = beanFactory.getBean("name-ref", DummyBean.class);

		assertEquals("some-name", nameRef.getName());
		assertEquals(nameValue, nameRef.getSpouse());
	}

	@Test
	public void typeIndexedValue() throws Exception {
		DefaultListableBeanFactory beanFactory = createFactory("simpleConstructorNamespaceHandlerTests.xml");
		DummyBean typeRef = beanFactory.getBean("indexed-value", DummyBean.class);

		assertEquals("at", typeRef.getName());
		assertEquals("austria", typeRef.getValue());
		assertEquals(10, typeRef.getAge());
	}

	@Test
	public void typeIndexedRef() throws Exception {
		DefaultListableBeanFactory beanFactory = createFactory("simpleConstructorNamespaceHandlerTests.xml");
		DummyBean typeRef = beanFactory.getBean("indexed-ref", DummyBean.class);

		assertEquals("some-name", typeRef.getName());
		assertEquals(beanFactory.getBean("name-value"), typeRef.getSpouse());
	}

	@Test(expected = BeanDefinitionStoreException.class)
	public void ambiguousConstructor() throws Exception {
		DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
		new XmlBeanDefinitionReader(bf).loadBeanDefinitions(
				new ClassPathResource("simpleConstructorNamespaceHandlerTestsWithErrors.xml", getClass()));
	}

	@Test
	public void constructorWithNameEndingInRef() throws Exception {
		DefaultListableBeanFactory beanFactory = createFactory("simpleConstructorNamespaceHandlerTests.xml");
		DummyBean derivedBean = beanFactory.getBean("beanWithRefConstructorArg", DummyBean.class);
		assertEquals(10, derivedBean.getAge());
		assertEquals("silly name", derivedBean.getName());
	}

	private DefaultListableBeanFactory createFactory(String resourceName) {
		DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
		new XmlBeanDefinitionReader(bf).loadBeanDefinitions(
				new ClassPathResource(resourceName, getClass()));
		return bf;
	}
}
