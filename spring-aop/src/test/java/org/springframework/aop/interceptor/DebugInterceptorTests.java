

package org.springframework.aop.interceptor;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

/**
 * Unit tests for the {@link DebugInterceptor} class.
 *
 * @author Rick Evans

 */
public class DebugInterceptorTests {

	@Test
	public void testSunnyDayPathLogsCorrectly() throws Throwable {

		MethodInvocation methodInvocation = mock(MethodInvocation.class);

		Log log = mock(Log.class);
		given(log.isTraceEnabled()).willReturn(true);

		DebugInterceptor interceptor = new StubDebugInterceptor(log);
		interceptor.invoke(methodInvocation);
		checkCallCountTotal(interceptor);

		verify(log, times(2)).trace(anyString());
	}

	@Test
	public void testExceptionPathStillLogsCorrectly() throws Throwable {

		MethodInvocation methodInvocation = mock(MethodInvocation.class);

		IllegalArgumentException exception = new IllegalArgumentException();
		given(methodInvocation.proceed()).willThrow(exception);

		Log log = mock(Log.class);
		given(log.isTraceEnabled()).willReturn(true);

		DebugInterceptor interceptor = new StubDebugInterceptor(log);
		try {
			interceptor.invoke(methodInvocation);
			fail("Must have propagated the IllegalArgumentException.");
		}
		catch (IllegalArgumentException expected) {
		}
		checkCallCountTotal(interceptor);

		verify(log).trace(anyString());
		verify(log).trace(anyString(), eq(exception));
	}

	private void checkCallCountTotal(DebugInterceptor interceptor) {
		assertEquals("Intercepted call count not being incremented correctly", 1, interceptor.getCount());
	}


	@SuppressWarnings("serial")
	private static final class StubDebugInterceptor extends DebugInterceptor {

		private final Log log;


		public StubDebugInterceptor(Log log) {
			super(true);
			this.log = log;
		}

		@Override
		protected Log getLoggerForInvocation(MethodInvocation invocation) {
			return log;
		}

	}

}
