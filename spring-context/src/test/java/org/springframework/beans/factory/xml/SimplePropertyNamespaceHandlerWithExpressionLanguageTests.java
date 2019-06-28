

package org.springframework.beans.factory.xml;

import org.junit.Test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.tests.sample.beans.ITestBean;

import static org.junit.Assert.*;

/**
 * Tests for combining the expression language and the p namespace. Due to the required EL dependency, this test is in
 * context module rather than the beans module.
 *
 * @author Arjen Poutsma
 */
public class SimplePropertyNamespaceHandlerWithExpressionLanguageTests {

	@Test
	public void combineWithExpressionLanguage() {
		ApplicationContext applicationContext =
				new ClassPathXmlApplicationContext("simplePropertyNamespaceHandlerWithExpressionLanguageTests.xml",
						getClass());
		ITestBean foo = applicationContext.getBean("foo", ITestBean.class);
		ITestBean bar = applicationContext.getBean("bar", ITestBean.class);
		assertEquals("Invalid name", "Baz", foo.getName());
		assertEquals("Invalid name", "Baz", bar.getName());
	}

}
