

package org.springframework.aop.framework;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;

import org.springframework.tests.sample.beans.TestBean;

import static org.junit.Assert.*;

/**
 * @author Rod Johnson
 * @author Chris Beams
 * @since 14.03.2003
 */
public class MethodInvocationTests {

	@Test
	public void testValidInvocation() throws Throwable {
		Method m = Object.class.getMethod("hashCode");
		Object proxy = new Object();
		final Object returnValue = new Object();
		List<Object> is = new LinkedList<>();
		is.add(new MethodInterceptor() {
			@Override
			public Object invoke(MethodInvocation invocation) throws Throwable {
				return returnValue;
			}
		});
			ReflectiveMethodInvocation invocation = new ReflectiveMethodInvocation(proxy, null, //?
		m, null, null, is // list
	);
		Object rv = invocation.proceed();
		assertTrue("correct response", rv == returnValue);
	}

	/**
	 * toString on target can cause failure.
	 */
	@Test
	public void testToStringDoesntHitTarget() throws Throwable {
		Object target = new TestBean() {
			@Override
			public String toString() {
				throw new UnsupportedOperationException("toString");
			}
		};
		List<Object> is = new LinkedList<>();

		Method m = Object.class.getMethod("hashCode");
		Object proxy = new Object();
		ReflectiveMethodInvocation invocation =
			new ReflectiveMethodInvocation(proxy, target, m, null, null, is);

		// If it hits target, the test will fail with the UnsupportedOpException
		// in the inner class above.
		invocation.toString();
	}

}
