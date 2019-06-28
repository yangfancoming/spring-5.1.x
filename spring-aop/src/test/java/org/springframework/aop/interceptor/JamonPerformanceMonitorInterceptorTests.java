

package org.springframework.aop.interceptor;

import com.jamonapi.MonitorFactory;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

/**
 * @author Steve Souza
 * @since 4.1
 */
public class JamonPerformanceMonitorInterceptorTests {

	private final JamonPerformanceMonitorInterceptor interceptor = new JamonPerformanceMonitorInterceptor();

	private final MethodInvocation mi = mock(MethodInvocation.class);

	private final Log log = mock(Log.class);


	@Before
	public void setUp() {
		MonitorFactory.reset();
	}

	@After
	public void tearDown() {
		MonitorFactory.reset();
	}


	@Test
	public void testInvokeUnderTraceWithNormalProcessing() throws Throwable {
		given(mi.getMethod()).willReturn(String.class.getMethod("toString"));

		interceptor.invokeUnderTrace(mi, log);

		assertTrue("jamon must track the method being invoked", MonitorFactory.getNumRows() > 0);
		assertTrue("The jamon report must contain the toString method that was invoked",
				MonitorFactory.getReport().contains("toString"));
	}

	@Test
	public void testInvokeUnderTraceWithExceptionTracking() throws Throwable {
		given(mi.getMethod()).willReturn(String.class.getMethod("toString"));
		given(mi.proceed()).willThrow(new IllegalArgumentException());

		try {
			interceptor.invokeUnderTrace(mi, log);
			fail("Must have propagated the IllegalArgumentException");
		}
		catch (IllegalArgumentException expected) {
		}

		assertEquals("Monitors must exist for the method invocation and 2 exceptions",
				3, MonitorFactory.getNumRows());
		assertTrue("The jamon report must contain the toString method that was invoked",
				MonitorFactory.getReport().contains("toString"));
		assertTrue("The jamon report must contain the generic exception: " + MonitorFactory.EXCEPTIONS_LABEL,
				MonitorFactory.getReport().contains(MonitorFactory.EXCEPTIONS_LABEL));
		assertTrue("The jamon report must contain the specific exception: IllegalArgumentException'",
				MonitorFactory.getReport().contains("IllegalArgumentException"));
	}

}
