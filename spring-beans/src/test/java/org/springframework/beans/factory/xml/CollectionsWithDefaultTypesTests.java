

package org.springframework.beans.factory.xml;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.tests.sample.beans.TestBean;

import static org.junit.Assert.*;

/**
 * @author Rob Harrop
 * @author Juergen Hoeller
 */
public class CollectionsWithDefaultTypesTests {

	private final DefaultListableBeanFactory beanFactory;

	public CollectionsWithDefaultTypesTests() {
		this.beanFactory = new DefaultListableBeanFactory();
		new XmlBeanDefinitionReader(this.beanFactory).loadBeanDefinitions(
				new ClassPathResource("collectionsWithDefaultTypes.xml", getClass()));
	}

	@Test
	public void testListHasDefaultType() throws Exception {
		TestBean bean = (TestBean) this.beanFactory.getBean("testBean");
		for (Object o : bean.getSomeList()) {
			assertEquals("Value type is incorrect", Integer.class, o.getClass());
		}
	}

	@Test
	public void testSetHasDefaultType() throws Exception {
		TestBean bean = (TestBean) this.beanFactory.getBean("testBean");
		for (Object o : bean.getSomeSet()) {
			assertEquals("Value type is incorrect", Integer.class, o.getClass());
		}
	}

	@Test
	public void testMapHasDefaultKeyAndValueType() throws Exception {
		TestBean bean = (TestBean) this.beanFactory.getBean("testBean");
		assertMap(bean.getSomeMap());
	}

	@Test
	public void testMapWithNestedElementsHasDefaultKeyAndValueType() throws Exception {
		TestBean bean = (TestBean) this.beanFactory.getBean("testBean2");
		assertMap(bean.getSomeMap());
	}

	private void assertMap(Map<?,?> map) {
		for (Map.Entry entry : map.entrySet()) {
			assertEquals("Key type is incorrect", Integer.class, entry.getKey().getClass());
			assertEquals("Value type is incorrect", Boolean.class, entry.getValue().getClass());
		}
	}

	@Test
	public void testBuildCollectionFromMixtureOfReferencesAndValues() throws Exception {
		MixedCollectionBean jumble = (MixedCollectionBean) this.beanFactory.getBean("jumble");
		assertTrue("Expected 3 elements, not " + jumble.getJumble().size(),
				jumble.getJumble().size() == 3);
		List l = (List) jumble.getJumble();
		assertTrue(l.get(0).equals("literal"));
		Integer[] array1 = (Integer[]) l.get(1);
		assertTrue(array1[0].equals(new Integer(2)));
		assertTrue(array1[1].equals(new Integer(4)));
		int[] array2 = (int[]) l.get(2);
		assertTrue(array2[0] == 3);
		assertTrue(array2[1] == 5);
	}

}
