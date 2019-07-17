

package org.springframework.aop.config;

import org.junit.Test;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

import static org.junit.Assert.*;
import static org.springframework.tests.TestResourceUtils.*;

/**
 * Tests that the &lt;aop:config/&gt; element can be used as a top level element.
 *
 * @author Rob Harrop

 */
public class TopLevelAopTagTests {

	@Test
	public void testParse() {
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		new XmlBeanDefinitionReader(beanFactory).loadBeanDefinitions(
				qualifiedResource(TopLevelAopTagTests.class, "context.xml"));

		assertTrue(beanFactory.containsBeanDefinition("testPointcut"));
	}

}
