

package org.springframework.aop.config;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.BeanFactory;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

/**
 * @author Rick Evans

 */
public class MethodLocatingFactoryBeanTests {

	private static final String BEAN_NAME = "string";
	private MethodLocatingFactoryBean factory;
	private BeanFactory beanFactory;

	@Before
	public void setUp() {
		factory = new MethodLocatingFactoryBean();
		beanFactory = mock(BeanFactory.class);
	}

	@Test
	public void testIsSingleton() {
		assertTrue(factory.isSingleton());
	}

	@Test
	public void testGetObjectType() {
		assertEquals(Method.class, factory.getObjectType());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWithNullTargetBeanName() {
		factory.setMethodName("toString()");
		factory.setBeanFactory(beanFactory);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWithEmptyTargetBeanName() {
		factory.setTargetBeanName("");
		factory.setMethodName("toString()");
		factory.setBeanFactory(beanFactory);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWithNullTargetMethodName() {
		factory.setTargetBeanName(BEAN_NAME);
		factory.setBeanFactory(beanFactory);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWithEmptyTargetMethodName() {
		factory.setTargetBeanName(BEAN_NAME);
		factory.setMethodName("");
		factory.setBeanFactory(beanFactory);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWhenTargetBeanClassCannotBeResolved() {
		factory.setTargetBeanName(BEAN_NAME);
		factory.setMethodName("toString()");
		factory.setBeanFactory(beanFactory);
		verify(beanFactory).getType(BEAN_NAME);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSunnyDayPath() throws Exception {
		given(beanFactory.getType(BEAN_NAME)).willReturn((Class)String.class);
		factory.setTargetBeanName(BEAN_NAME);
		factory.setMethodName("toString()");
		factory.setBeanFactory(beanFactory);
		Object result = factory.getObject();
		assertNotNull(result);
		assertTrue(result instanceof Method);
		Method method = (Method) result;
		assertEquals("Bingo", method.invoke("Bingo"));
	}

	@Test(expected = IllegalArgumentException.class)
	@SuppressWarnings("unchecked")
	public void testWhereMethodCannotBeResolved() {
		given(beanFactory.getType(BEAN_NAME)).willReturn((Class)String.class);
		factory.setTargetBeanName(BEAN_NAME);
		factory.setMethodName("loadOfOld()");
		factory.setBeanFactory(beanFactory);
	}

}
