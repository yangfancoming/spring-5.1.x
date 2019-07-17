

package org.springframework.beans.factory.config;

import java.sql.Connection;

import org.junit.Test;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.tests.sample.beans.TestBean;

import static org.junit.Assert.*;
import static org.springframework.tests.TestResourceUtils.*;

/**
 * Unit tests for {@link FieldRetrievingFactoryBean}.
 *


 * @since 31.07.2004
 */
public class FieldRetrievingFactoryBeanTests {

	@Test
	public void testStaticField() throws Exception {
		FieldRetrievingFactoryBean fr = new FieldRetrievingFactoryBean();
		fr.setStaticField("java.sql.Connection.TRANSACTION_SERIALIZABLE");
		fr.afterPropertiesSet();
		assertEquals(new Integer(Connection.TRANSACTION_SERIALIZABLE), fr.getObject());
	}

	@Test
	public void testStaticFieldWithWhitespace() throws Exception {
		FieldRetrievingFactoryBean fr = new FieldRetrievingFactoryBean();
		fr.setStaticField("  java.sql.Connection.TRANSACTION_SERIALIZABLE  ");
		fr.afterPropertiesSet();
		assertEquals(new Integer(Connection.TRANSACTION_SERIALIZABLE), fr.getObject());
	}

	@Test
	public void testStaticFieldViaClassAndFieldName() throws Exception {
		FieldRetrievingFactoryBean fr = new FieldRetrievingFactoryBean();
		fr.setTargetClass(Connection.class);
		fr.setTargetField("TRANSACTION_SERIALIZABLE");
		fr.afterPropertiesSet();
		assertEquals(new Integer(Connection.TRANSACTION_SERIALIZABLE), fr.getObject());
	}

	@Test
	public void testNonStaticField() throws Exception {
		FieldRetrievingFactoryBean fr = new FieldRetrievingFactoryBean();
		PublicFieldHolder target = new PublicFieldHolder();
		fr.setTargetObject(target);
		fr.setTargetField("publicField");
		fr.afterPropertiesSet();
		assertEquals(target.publicField, fr.getObject());
	}

	@Test
	public void testNothingButBeanName() throws Exception {
		FieldRetrievingFactoryBean fr = new FieldRetrievingFactoryBean();
		fr.setBeanName("java.sql.Connection.TRANSACTION_SERIALIZABLE");
		fr.afterPropertiesSet();
		assertEquals(new Integer(Connection.TRANSACTION_SERIALIZABLE), fr.getObject());
	}

	@Test
	public void testJustTargetField() throws Exception {
		FieldRetrievingFactoryBean fr = new FieldRetrievingFactoryBean();
		fr.setTargetField("TRANSACTION_SERIALIZABLE");
		try {
			fr.afterPropertiesSet();
		}
		catch (IllegalArgumentException expected) {
		}
	}

	@Test
	public void testJustTargetClass() throws Exception {
		FieldRetrievingFactoryBean fr = new FieldRetrievingFactoryBean();
		fr.setTargetClass(Connection.class);
		try {
			fr.afterPropertiesSet();
		}
		catch (IllegalArgumentException expected) {
		}
	}

	@Test
	public void testJustTargetObject() throws Exception {
		FieldRetrievingFactoryBean fr = new FieldRetrievingFactoryBean();
		fr.setTargetObject(new PublicFieldHolder());
		try {
			fr.afterPropertiesSet();
		}
		catch (IllegalArgumentException expected) {
		}
	}

	@Test
	public void testWithConstantOnClassWithPackageLevelVisibility() throws Exception {
		FieldRetrievingFactoryBean fr = new FieldRetrievingFactoryBean();
		fr.setBeanName("org.springframework.tests.sample.beans.PackageLevelVisibleBean.CONSTANT");
		fr.afterPropertiesSet();
		assertEquals("Wuby", fr.getObject());
	}

	@Test
	public void testBeanNameSyntaxWithBeanFactory() throws Exception {
		DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
		new XmlBeanDefinitionReader(bf).loadBeanDefinitions(
				qualifiedResource(FieldRetrievingFactoryBeanTests.class, "context.xml"));

		TestBean testBean = (TestBean) bf.getBean("testBean");
		assertEquals(new Integer(Connection.TRANSACTION_SERIALIZABLE), testBean.getSomeIntegerArray()[0]);
		assertEquals(new Integer(Connection.TRANSACTION_SERIALIZABLE), testBean.getSomeIntegerArray()[1]);
	}


	private static class PublicFieldHolder {

		public String publicField = "test";
	}

}
