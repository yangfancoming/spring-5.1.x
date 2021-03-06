

package org.springframework.aop.framework;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.Resource;

import static org.junit.Assert.*;
import static org.springframework.tests.TestResourceUtils.*;

/**
 * @since 03.09.2004
 */
public class PrototypeTargetTests {

	private static final Resource CONTEXT = qualifiedResource(PrototypeTargetTests.class, "context.xml");


	@Test
	public void testPrototypeProxyWithPrototypeTarget() {
		TestBeanImpl.constructionCount = 0;
		DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
		new XmlBeanDefinitionReader(bf).loadBeanDefinitions(CONTEXT);
		for (int i = 0; i < 10; i++) {
			TestBean tb = (TestBean) bf.getBean("testBeanPrototype");
			tb.doSomething();
		}
		TestInterceptor interceptor = (TestInterceptor) bf.getBean("testInterceptor");
		assertEquals(10, TestBeanImpl.constructionCount);
		assertEquals(10, interceptor.invocationCount);
	}

	@Test
	public void testSingletonProxyWithPrototypeTarget() {
		TestBeanImpl.constructionCount = 0;
		DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
		new XmlBeanDefinitionReader(bf).loadBeanDefinitions(CONTEXT);
		for (int i = 0; i < 10; i++) {
			TestBean tb = (TestBean) bf.getBean("testBeanSingleton");
			tb.doSomething();
		}
		TestInterceptor interceptor = (TestInterceptor) bf.getBean("testInterceptor");
		assertEquals(1, TestBeanImpl.constructionCount);
		assertEquals(10, interceptor.invocationCount);
	}


	public interface TestBean {

		void doSomething();
	}


	public static class TestBeanImpl implements TestBean {

		private static int constructionCount = 0;

		public TestBeanImpl() {
			constructionCount++;
		}

		@Override
		public void doSomething() {
		}
	}


	public static class TestInterceptor implements MethodInterceptor {

		private int invocationCount = 0;

		@Override
		public Object invoke(MethodInvocation methodInvocation) throws Throwable {
			invocationCount++;
			return methodInvocation.proceed();
		}
	}

}
