

package org.springframework.aop.config;

import org.junit.Test;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

import static org.junit.Assert.*;
import static org.springframework.tests.TestResourceUtils.*;

/**
 * @author Mark Fisher

 */
public class AopNamespaceHandlerPointcutErrorTests {

	@Test
	public void testDuplicatePointcutConfig() {
		try {
			DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
			new XmlBeanDefinitionReader(bf).loadBeanDefinitions(
					qualifiedResource(getClass(), "pointcutDuplication.xml"));
			fail("parsing should have caused a BeanDefinitionStoreException");
		}
		catch (BeanDefinitionStoreException ex) {
			assertTrue(ex.contains(BeanDefinitionParsingException.class));
		}
	}

	@Test
	public void testMissingPointcutConfig() {
		try {
			DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
			new XmlBeanDefinitionReader(bf).loadBeanDefinitions(
					qualifiedResource(getClass(), "pointcutMissing.xml"));
			fail("parsing should have caused a BeanDefinitionStoreException");
		}
		catch (BeanDefinitionStoreException ex) {
			assertTrue(ex.contains(BeanDefinitionParsingException.class));
		}
	}

}
