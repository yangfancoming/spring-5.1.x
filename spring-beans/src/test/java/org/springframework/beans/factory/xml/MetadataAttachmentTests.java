

package org.springframework.beans.factory.xml;

import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.*;


public class MetadataAttachmentTests {

	private DefaultListableBeanFactory beanFactory;


	@Before
	public void setUp() throws Exception {
		this.beanFactory = new DefaultListableBeanFactory();
		new XmlBeanDefinitionReader(this.beanFactory).loadBeanDefinitions(
				new ClassPathResource("withMeta.xml", getClass()));
	}

	@Test
	public void metadataAttachment() throws Exception {
		BeanDefinition beanDefinition1 = this.beanFactory.getMergedBeanDefinition("testBean1");
		assertEquals("bar", beanDefinition1.getAttribute("foo"));
	}

	@Test
	public void metadataIsInherited() throws Exception {
		BeanDefinition beanDefinition = this.beanFactory.getMergedBeanDefinition("testBean2");
		assertEquals("Metadata not inherited", "bar", beanDefinition.getAttribute("foo"));
		assertEquals("Child metdata not attached", "123", beanDefinition.getAttribute("abc"));
	}

	@Test
	public void propertyMetadata() throws Exception {
		BeanDefinition beanDefinition = this.beanFactory.getMergedBeanDefinition("testBean3");
		PropertyValue pv = beanDefinition.getPropertyValues().getPropertyValue("name");
		assertEquals("Harrop", pv.getAttribute("surname"));
	}

}
