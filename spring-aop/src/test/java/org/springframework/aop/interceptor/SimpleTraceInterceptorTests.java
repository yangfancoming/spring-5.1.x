

package org.springframework.aop.interceptor;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

/**
 * Unit tests for the {@link SimpleTraceInterceptor} class.
 *
 * @author Rick Evans
 * @author Chris Beams
 */
public class SimpleTraceInterceptorTests {

	@Test
	public void testSunnyDayPathLogsCorrectly() throws Throwable {
		MethodInvocation mi = mock(MethodInvocation.class);
		given(mi.getMethod()).willReturn(String.class.getMethod("toString"));
		given(mi.getThis()).willReturn(this);

		Log log = mock(Log.class);

		SimpleTraceInterceptor interceptor = new SimpleTraceInterceptor(true);
		interceptor.invokeUnderTrace(mi, log);

		verify(log, times(2)).trace(anyString());
	}

	@Test
	public void testExceptionPathStillLogsCorrectly() throws Throwable {
		MethodInvocation mi = mock(MethodInvocation.class);
		given(mi.getMethod()).willReturn(String.class.getMethod("toString"));
		given(mi.getThis()).willReturn(this);
		IllegalArgumentException exception = new IllegalArgumentException();
		given(mi.proceed()).willThrow(exception);

		Log log = mock(Log.class);

		final SimpleTraceInterceptor interceptor = new SimpleTraceInterceptor(true);

		try {
			interceptor.invokeUnderTrace(mi, log);
			fail("Must have propagated the IllegalArgumentException.");
		}
		catch (IllegalArgumentException expected) {
		}

		verify(log).trace(anyString());
		verify(log).trace(anyString(), eq(exception));
	}

}
