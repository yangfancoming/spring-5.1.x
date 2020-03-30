

package org.springframework.beans.factory.xml;

import java.util.Arrays;

import org.junit.Test;
import org.xml.sax.InputSource;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.util.ObjectUtils;

import static org.junit.Assert.*;


public class XmlBeanDefinitionReaderTests {

	SimpleBeanDefinitionRegistry registry = new SimpleBeanDefinitionRegistry();
	XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(registry);
	Resource resource = new InputStreamResource(getClass().getResourceAsStream("test.xml"));

	@Test
	public void setParserClassSunnyDay() {
		xmlBeanDefinitionReader.setDocumentReaderClass(DefaultBeanDefinitionDocumentReader.class);
	}

	@Test(expected = BeanDefinitionStoreException.class)
	public void withOpenInputStream() {
		xmlBeanDefinitionReader.loadBeanDefinitions(resource);
	}

	@Test
	public void withOpenInputStreamAndExplicitValidationMode() {
		xmlBeanDefinitionReader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_DTD);
		xmlBeanDefinitionReader.loadBeanDefinitions(resource);
		testBeanDefinitions(registry);
	}

	@Test
	public void withImport() {
		Resource resource = new ClassPathResource("import.xml", getClass());
		xmlBeanDefinitionReader.loadBeanDefinitions(resource);
		testBeanDefinitions(registry);
	}

	@Test
	public void withWildcardImport() {
		Resource resource = new ClassPathResource("importPattern.xml", getClass());
		xmlBeanDefinitionReader.loadBeanDefinitions(resource);
		testBeanDefinitions(registry);
	}

	@Test(expected = BeanDefinitionStoreException.class)
	public void withInputSource() {
		InputSource resource = new InputSource(getClass().getResourceAsStream("test.xml"));
		xmlBeanDefinitionReader.loadBeanDefinitions(resource);
	}

	@Test
	public void withInputSourceAndExplicitValidationMode() {
		InputSource resource = new InputSource(getClass().getResourceAsStream("test.xml"));
		xmlBeanDefinitionReader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_DTD);
		xmlBeanDefinitionReader.loadBeanDefinitions(resource);
		testBeanDefinitions(registry);
	}

	@Test
	public void withFreshInputStream() {
		Resource resource = new ClassPathResource("test.xml", getClass());
		xmlBeanDefinitionReader.loadBeanDefinitions(resource);
		testBeanDefinitions(registry);
	}

	private void testBeanDefinitions(BeanDefinitionRegistry registry) {
		assertEquals(24, registry.getBeanDefinitionCount());
		assertEquals(24, registry.getBeanDefinitionNames().length);
		assertTrue(Arrays.asList(registry.getBeanDefinitionNames()).contains("rod"));
		assertTrue(Arrays.asList(registry.getBeanDefinitionNames()).contains("aliased"));
		assertTrue(registry.containsBeanDefinition("rod"));
		assertTrue(registry.containsBeanDefinition("aliased"));
		assertEquals(TestBean.class.getName(), registry.getBeanDefinition("rod").getBeanClassName());
		assertEquals(TestBean.class.getName(), registry.getBeanDefinition("aliased").getBeanClassName());
		assertTrue(registry.isAlias("youralias"));
		String[] aliases = registry.getAliases("aliased");
		assertEquals(2, aliases.length);
		assertTrue(ObjectUtils.containsElement(aliases, "myalias"));
		assertTrue(ObjectUtils.containsElement(aliases, "youralias"));
	}

	@Test
	public void dtdValidationAutodetect() {
		doTestValidation("validateWithDtd.xml");
	}

	@Test
	public void xsdValidationAutodetect() {
		doTestValidation("validateWithXsd.xml");
	}

	private void doTestValidation(String resourceName) {
		DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
		Resource resource = new ClassPathResource(resourceName, getClass());
		new XmlBeanDefinitionReader(factory).loadBeanDefinitions(resource);
		TestBean bean = (TestBean) factory.getBean("testBean");
		assertNotNull(bean);
	}

}
