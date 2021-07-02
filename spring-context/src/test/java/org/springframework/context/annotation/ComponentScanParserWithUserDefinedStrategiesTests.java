package org.springframework.context.annotation;

import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.*;


public class ComponentScanParserWithUserDefinedStrategiesTests {

	@Test
	public void testCustomBeanNameGenerator() {
		ApplicationContext context = new ClassPathXmlApplicationContext("org/springframework/context/annotation/customNameGeneratorTests.xml");
		assertTrue(context.containsBean("testing.fooServiceImpl"));
	}

	@Test
	public void testCustomScopeMetadataResolver() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("org/springframework/context/annotation/customScopeResolverTests.xml");
		BeanDefinition bd = context.getBeanFactory().getBeanDefinition("fooServiceImpl");
		assertEquals("myCustomScope", bd.getScope());
		assertFalse(bd.isSingleton());
	}

	@Test
	public void testInvalidConstructorBeanNameGenerator() {
		try {
			new ClassPathXmlApplicationContext("org/springframework/context/annotation/invalidConstructorNameGeneratorTests.xml");
			fail("should have failed: no-arg constructor is required");
		}catch (BeansException ex) {
			// expected
		}
	}

	@Test
	public void testInvalidClassNameScopeMetadataResolver() {
		try {
			new ClassPathXmlApplicationContext("org/springframework/context/annotation/invalidClassNameScopeResolverTests.xml");
			fail("should have failed: no such class");
		}catch (BeansException ex) {
			// expected
		}
	}

}
