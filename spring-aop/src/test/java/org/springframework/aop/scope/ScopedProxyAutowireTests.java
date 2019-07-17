

package org.springframework.aop.scope;

import java.util.Arrays;

import org.junit.Test;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

import static org.junit.Assert.*;
import static org.springframework.tests.TestResourceUtils.*;


public class ScopedProxyAutowireTests {

	@Test
	public void testScopedProxyInheritsAutowireCandidateFalse() {
		DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
		new XmlBeanDefinitionReader(bf).loadBeanDefinitions(
				qualifiedResource(ScopedProxyAutowireTests.class, "scopedAutowireFalse.xml"));

		assertTrue(Arrays.asList(bf.getBeanNamesForType(TestBean.class, false, false)).contains("scoped"));
		assertTrue(Arrays.asList(bf.getBeanNamesForType(TestBean.class, true, false)).contains("scoped"));
		assertFalse(bf.containsSingleton("scoped"));
		TestBean autowired = (TestBean) bf.getBean("autowired");
		TestBean unscoped = (TestBean) bf.getBean("unscoped");
		assertSame(unscoped, autowired.getChild());
	}

	@Test
	public void testScopedProxyReplacesAutowireCandidateTrue() {
		DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
		new XmlBeanDefinitionReader(bf).loadBeanDefinitions(
				qualifiedResource(ScopedProxyAutowireTests.class, "scopedAutowireTrue.xml"));

		assertTrue(Arrays.asList(bf.getBeanNamesForType(TestBean.class, true, false)).contains("scoped"));
		assertTrue(Arrays.asList(bf.getBeanNamesForType(TestBean.class, false, false)).contains("scoped"));
		assertFalse(bf.containsSingleton("scoped"));
		TestBean autowired = (TestBean) bf.getBean("autowired");
		TestBean scoped = (TestBean) bf.getBean("scoped");
		assertSame(scoped, autowired.getChild());
	}


	static class TestBean {

		private TestBean child;

		public void setChild(TestBean child) {
			this.child = child;
		}

		public TestBean getChild() {
			return this.child;
		}
	}

}
