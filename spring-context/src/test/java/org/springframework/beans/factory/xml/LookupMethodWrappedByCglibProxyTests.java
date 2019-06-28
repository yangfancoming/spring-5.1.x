

package org.springframework.beans.factory.xml;

import org.junit.Before;
import org.junit.Test;

import org.springframework.aop.interceptor.DebugInterceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.tests.sample.beans.ITestBean;

import static org.junit.Assert.*;

/**
 * Tests lookup methods wrapped by a CGLIB proxy (see SPR-391).
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class LookupMethodWrappedByCglibProxyTests {

	private static final Class<?> CLASS = LookupMethodWrappedByCglibProxyTests.class;
	private static final String CLASSNAME = CLASS.getSimpleName();

	private static final String CONTEXT = CLASSNAME + "-context.xml";

	private ApplicationContext applicationContext;

	@Before
	public void setUp() {
		this.applicationContext = new ClassPathXmlApplicationContext(CONTEXT, CLASS);
		resetInterceptor();
	}

	@Test
	public void testAutoProxiedLookup() {
		OverloadLookup olup = (OverloadLookup) applicationContext.getBean("autoProxiedOverload");
		ITestBean jenny = olup.newTestBean();
		assertEquals("Jenny", jenny.getName());
		assertEquals("foo", olup.testMethod());
		assertInterceptorCount(2);
	}

	@Test
	public void testRegularlyProxiedLookup() {
		OverloadLookup olup = (OverloadLookup) applicationContext.getBean("regularlyProxiedOverload");
		ITestBean jenny = olup.newTestBean();
		assertEquals("Jenny", jenny.getName());
		assertEquals("foo", olup.testMethod());
		assertInterceptorCount(2);
	}

	private void assertInterceptorCount(int count) {
		DebugInterceptor interceptor = getInterceptor();
		assertEquals("Interceptor count is incorrect", count, interceptor.getCount());
	}

	private void resetInterceptor() {
		DebugInterceptor interceptor = getInterceptor();
		interceptor.resetCount();
	}

	private DebugInterceptor getInterceptor() {
		return (DebugInterceptor) applicationContext.getBean("interceptor");
	}

}


abstract class OverloadLookup {

	public abstract ITestBean newTestBean();

	public String testMethod() {
		return "foo";
	}
}

