

package com.foo;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;


public class ComponentBeanDefinitionParserTests {

	private static DefaultListableBeanFactory bf;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		bf = new DefaultListableBeanFactory();
		new XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource("com/foo/component-config.xml"));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		bf.destroySingletons();
	}

	private Component getBionicFamily() {
		return bf.getBean("bionic-family", Component.class);
	}

	@Test
	public void testBionicBasic() throws Exception {
		Component cp = getBionicFamily();
		assertThat("Bionic-1", equalTo(cp.getName()));
	}

	@Test
	public void testBionicFirstLevelChildren() throws Exception {
		Component cp = getBionicFamily();
		List<Component> components = cp.getComponents();
		assertThat(2, equalTo(components.size()));
		assertThat("Mother-1", equalTo(components.get(0).getName()));
		assertThat("Rock-1", equalTo(components.get(1).getName()));
	}

	@Test
	public void testBionicSecondLevelChildren() throws Exception {
		Component cp = getBionicFamily();
		List<Component> components = cp.getComponents().get(0).getComponents();
		assertThat(2, equalTo(components.size()));
		assertThat("Karate-1", equalTo(components.get(0).getName()));
		assertThat("Sport-1", equalTo(components.get(1).getName()));
	}
}
