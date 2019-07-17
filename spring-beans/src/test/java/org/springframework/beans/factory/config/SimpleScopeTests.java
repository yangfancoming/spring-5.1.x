

package org.springframework.beans.factory.config;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.tests.sample.beans.TestBean;

import static org.junit.Assert.*;
import static org.springframework.tests.TestResourceUtils.*;

/**
 * Simple test to illustrate and verify scope usage.
 *
 * @author Rod Johnson


 */
public class SimpleScopeTests {

	private DefaultListableBeanFactory beanFactory;


	@Before
	public void setup() {
		beanFactory = new DefaultListableBeanFactory();
		Scope scope = new NoOpScope() {
			private int index;
			private List<TestBean> objects = new LinkedList<>(); {
				objects.add(new TestBean());
				objects.add(new TestBean());
			}
			@Override
			public Object get(String name, ObjectFactory<?> objectFactory) {
				if (index >= objects.size()) {
					index = 0;
				}
				return objects.get(index++);
			}
		};

		beanFactory.registerScope("myScope", scope);

		String[] scopeNames = beanFactory.getRegisteredScopeNames();
		assertEquals(1, scopeNames.length);
		assertEquals("myScope", scopeNames[0]);
		assertSame(scope, beanFactory.getRegisteredScope("myScope"));

		new XmlBeanDefinitionReader(beanFactory).loadBeanDefinitions(
				qualifiedResource(SimpleScopeTests.class, "context.xml"));
	}


	@Test
	public void testCanGetScopedObject() {
		TestBean tb1 = (TestBean) beanFactory.getBean("usesScope");
		TestBean tb2 = (TestBean) beanFactory.getBean("usesScope");
		assertNotSame(tb1, tb2);
		TestBean tb3 = (TestBean) beanFactory.getBean("usesScope");
		assertSame(tb3, tb1);
	}

}
