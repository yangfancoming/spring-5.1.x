

package org.springframework.beans.factory.config;

import java.util.Properties;

import org.junit.Test;

import org.springframework.core.io.Resource;

import static org.junit.Assert.*;
import static org.springframework.tests.TestResourceUtils.*;

/**
 * Unit tests for {@link PropertiesFactoryBean}.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 01.11.2003
 */
public class PropertiesFactoryBeanTests {

	private static final Class<?> CLASS = PropertiesFactoryBeanTests.class;
	private static final Resource TEST_PROPS = qualifiedResource(CLASS, "test.properties");
	private static final Resource TEST_PROPS_XML = qualifiedResource(CLASS, "test.properties.xml");

	@Test
	public void testWithPropertiesFile() throws Exception {
		PropertiesFactoryBean pfb = new PropertiesFactoryBean();
		pfb.setLocation(TEST_PROPS);
		pfb.afterPropertiesSet();
		Properties props = pfb.getObject();
		assertEquals("99", props.getProperty("tb.array[0].age"));
	}

	@Test
	public void testWithPropertiesXmlFile() throws Exception {
		PropertiesFactoryBean pfb = new PropertiesFactoryBean();
		pfb.setLocation(TEST_PROPS_XML);
		pfb.afterPropertiesSet();
		Properties props = pfb.getObject();
		assertEquals("99", props.getProperty("tb.array[0].age"));
	}

	@Test
	public void testWithLocalProperties() throws Exception {
		PropertiesFactoryBean pfb = new PropertiesFactoryBean();
		Properties localProps = new Properties();
		localProps.setProperty("key2", "value2");
		pfb.setProperties(localProps);
		pfb.afterPropertiesSet();
		Properties props = pfb.getObject();
		assertEquals("value2", props.getProperty("key2"));
	}

	@Test
	public void testWithPropertiesFileAndLocalProperties() throws Exception {
		PropertiesFactoryBean pfb = new PropertiesFactoryBean();
		pfb.setLocation(TEST_PROPS);
		Properties localProps = new Properties();
		localProps.setProperty("key2", "value2");
		localProps.setProperty("tb.array[0].age", "0");
		pfb.setProperties(localProps);
		pfb.afterPropertiesSet();
		Properties props = pfb.getObject();
		assertEquals("99", props.getProperty("tb.array[0].age"));
		assertEquals("value2", props.getProperty("key2"));
	}

	@Test
	public void testWithPropertiesFileAndMultipleLocalProperties() throws Exception {
		PropertiesFactoryBean pfb = new PropertiesFactoryBean();
		pfb.setLocation(TEST_PROPS);

		Properties props1 = new Properties();
		props1.setProperty("key2", "value2");
		props1.setProperty("tb.array[0].age", "0");

		Properties props2 = new Properties();
		props2.setProperty("spring", "framework");
		props2.setProperty("Don", "Mattingly");

		Properties props3 = new Properties();
		props3.setProperty("spider", "man");
		props3.setProperty("bat", "man");

		pfb.setPropertiesArray(new Properties[] {props1, props2, props3});
		pfb.afterPropertiesSet();

		Properties props = pfb.getObject();
		assertEquals("99", props.getProperty("tb.array[0].age"));
		assertEquals("value2", props.getProperty("key2"));
		assertEquals("framework", props.getProperty("spring"));
		assertEquals("Mattingly", props.getProperty("Don"));
		assertEquals("man", props.getProperty("spider"));
		assertEquals("man", props.getProperty("bat"));
	}

	@Test
	public void testWithPropertiesFileAndLocalPropertiesAndLocalOverride() throws Exception {
		PropertiesFactoryBean pfb = new PropertiesFactoryBean();
		pfb.setLocation(TEST_PROPS);
		Properties localProps = new Properties();
		localProps.setProperty("key2", "value2");
		localProps.setProperty("tb.array[0].age", "0");
		pfb.setProperties(localProps);
		pfb.setLocalOverride(true);
		pfb.afterPropertiesSet();
		Properties props = pfb.getObject();
		assertEquals("0", props.getProperty("tb.array[0].age"));
		assertEquals("value2", props.getProperty("key2"));
	}

	@Test
	public void testWithPrototype() throws Exception {
		PropertiesFactoryBean pfb = new PropertiesFactoryBean();
		pfb.setSingleton(false);
		pfb.setLocation(TEST_PROPS);
		Properties localProps = new Properties();
		localProps.setProperty("key2", "value2");
		pfb.setProperties(localProps);
		pfb.afterPropertiesSet();
		Properties props = pfb.getObject();
		assertEquals("99", props.getProperty("tb.array[0].age"));
		assertEquals("value2", props.getProperty("key2"));
		Properties newProps = pfb.getObject();
		assertTrue(props != newProps);
		assertEquals("99", newProps.getProperty("tb.array[0].age"));
		assertEquals("value2", newProps.getProperty("key2"));
	}

}
