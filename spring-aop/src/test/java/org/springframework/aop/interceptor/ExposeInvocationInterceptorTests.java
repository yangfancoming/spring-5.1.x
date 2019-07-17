

package org.springframework.aop.interceptor;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.TestBean;

import static org.junit.Assert.*;
import static org.springframework.tests.TestResourceUtils.*;

/**
 * Non-XML tests are in AbstractAopProxyTests
 *
 * @author Rod Johnson

 */
public class ExposeInvocationInterceptorTests {

	@Test
	public void testXmlConfig() {
		DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
		new XmlBeanDefinitionReader(bf).loadBeanDefinitions(
				qualifiedResource(ExposeInvocationInterceptorTests.class, "context.xml"));
		ITestBean tb = (ITestBean) bf.getBean("proxy");
		String name = "tony";
		tb.setName(name);
		// Fires context checks
		assertEquals(name, tb.getName());
	}

}


abstract class ExposedInvocationTestBean extends TestBean {

	@Override
	public String getName() {
		MethodInvocation invocation = ExposeInvocationInterceptor.currentInvocation();
		assertions(invocation);
		return super.getName();
	}

	@Override
	public void absquatulate() {
		MethodInvocation invocation = ExposeInvocationInterceptor.currentInvocation();
		assertions(invocation);
		super.absquatulate();
	}

	protected abstract void assertions(MethodInvocation invocation);
}


class InvocationCheckExposedInvocationTestBean extends ExposedInvocationTestBean {

	@Override
	protected void assertions(MethodInvocation invocation) {
		assertTrue(invocation.getThis() == this);
		assertTrue("Invocation should be on ITestBean: " + invocation.getMethod(),
				ITestBean.class.isAssignableFrom(invocation.getMethod().getDeclaringClass()));
	}
}
