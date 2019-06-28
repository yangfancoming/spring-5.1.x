

package org.springframework.beans.factory.xml;

import org.junit.Test;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.tests.sample.beans.TestBean;

import org.xml.sax.SAXParseException;

import static org.junit.Assert.*;

/**
 * @author Rob Harrop
 */
public class SchemaValidationTests {

	@Test
	public void withAutodetection() throws Exception {
		DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(bf);
		try {
			reader.loadBeanDefinitions(new ClassPathResource("invalidPerSchema.xml", getClass()));
			fail("Should not be able to parse a file with errors");
		}
		catch (BeansException ex) {
			assertTrue(ex.getCause() instanceof SAXParseException);
		}
	}

	@Test
	public void withExplicitValidationMode() throws Exception {
		DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(bf);
		reader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_XSD);
		try {
			reader.loadBeanDefinitions(new ClassPathResource("invalidPerSchema.xml", getClass()));
			fail("Should not be able to parse a file with errors");
		}
		catch (BeansException ex) {
			assertTrue(ex.getCause() instanceof SAXParseException);
		}
	}

	@Test
	public void loadDefinitions() throws Exception {
		DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(bf);
		reader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_XSD);
		reader.loadBeanDefinitions(new ClassPathResource("schemaValidated.xml", getClass()));

		TestBean foo = (TestBean) bf.getBean("fooBean");
		assertNotNull("Spouse is null", foo.getSpouse());
		assertEquals("Incorrect number of friends", 2, foo.getFriends().size());
	}

}
