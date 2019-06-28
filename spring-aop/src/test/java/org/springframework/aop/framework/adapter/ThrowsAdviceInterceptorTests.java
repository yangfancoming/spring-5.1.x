

package org.springframework.aop.framework.adapter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.rmi.ConnectException;
import java.rmi.RemoteException;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;

import org.springframework.aop.ThrowsAdvice;
import org.springframework.tests.aop.advice.MethodCounter;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

/**
 * @author Rod Johnson
 * @author Chris Beams
 */
public class ThrowsAdviceInterceptorTests {

	@Test(expected = IllegalArgumentException.class)
	public void testNoHandlerMethods() {
		// should require one handler method at least
		new ThrowsAdviceInterceptor(new Object());
	}

	@Test
	public void testNotInvoked() throws Throwable {
		MyThrowsHandler th = new MyThrowsHandler();
		ThrowsAdviceInterceptor ti = new ThrowsAdviceInterceptor(th);
		Object ret = new Object();
		MethodInvocation mi = mock(MethodInvocation.class);
		given(mi.proceed()).willReturn(ret);
		assertEquals(ret, ti.invoke(mi));
		assertEquals(0, th.getCalls());
	}

	@Test
	public void testNoHandlerMethodForThrowable() throws Throwable {
		MyThrowsHandler th = new MyThrowsHandler();
		ThrowsAdviceInterceptor ti = new ThrowsAdviceInterceptor(th);
		assertEquals(2, ti.getHandlerMethodCount());
		Exception ex = new Exception();
		MethodInvocation mi = mock(MethodInvocation.class);
		given(mi.proceed()).willThrow(ex);
		try {
			ti.invoke(mi);
			fail();
		}
		catch (Exception caught) {
			assertEquals(ex, caught);
		}
		assertEquals(0, th.getCalls());
	}

	@Test
	public void testCorrectHandlerUsed() throws Throwable {
		MyThrowsHandler th = new MyThrowsHandler();
		ThrowsAdviceInterceptor ti = new ThrowsAdviceInterceptor(th);
		FileNotFoundException ex = new FileNotFoundException();
		MethodInvocation mi = mock(MethodInvocation.class);
		given(mi.getMethod()).willReturn(Object.class.getMethod("hashCode"));
		given(mi.getThis()).willReturn(new Object());
		given(mi.proceed()).willThrow(ex);
		try {
			ti.invoke(mi);
			fail();
		}
		catch (Exception caught) {
			assertEquals(ex, caught);
		}
		assertEquals(1, th.getCalls());
		assertEquals(1, th.getCalls("ioException"));
	}

	@Test
	public void testCorrectHandlerUsedForSubclass() throws Throwable {
		MyThrowsHandler th = new MyThrowsHandler();
		ThrowsAdviceInterceptor ti = new ThrowsAdviceInterceptor(th);
		// Extends RemoteException
		ConnectException ex = new ConnectException("");
		MethodInvocation mi = mock(MethodInvocation.class);
		given(mi.proceed()).willThrow(ex);
		try {
			ti.invoke(mi);
			fail();
		}
		catch (Exception caught) {
			assertEquals(ex, caught);
		}
		assertEquals(1, th.getCalls());
		assertEquals(1, th.getCalls("remoteException"));
	}

	@Test
	public void testHandlerMethodThrowsException() throws Throwable {
		final Throwable t = new Throwable();

		@SuppressWarnings("serial")
		MyThrowsHandler th = new MyThrowsHandler() {
			@Override
			public void afterThrowing(RemoteException ex) throws Throwable {
				super.afterThrowing(ex);
				throw t;
			}
		};

		ThrowsAdviceInterceptor ti = new ThrowsAdviceInterceptor(th);
		// Extends RemoteException
		ConnectException ex = new ConnectException("");
		MethodInvocation mi = mock(MethodInvocation.class);
		given(mi.proceed()).willThrow(ex);
		try {
			ti.invoke(mi);
			fail();
		}
		catch (Throwable caught) {
			assertEquals(t, caught);
		}
		assertEquals(1, th.getCalls());
		assertEquals(1, th.getCalls("remoteException"));
	}


	@SuppressWarnings("serial")
	static class MyThrowsHandler extends MethodCounter implements ThrowsAdvice {

		// Full method signature
		public void afterThrowing(Method m, Object[] args, Object target, IOException ex) {
			count("ioException");
		}

		public void afterThrowing(RemoteException ex) throws Throwable {
			count("remoteException");
		}

		/** Not valid, wrong number of arguments */
		public void afterThrowing(Method m, Exception ex) throws Throwable {
			throw new UnsupportedOperationException("Shouldn't be called");
		}
	}

}
