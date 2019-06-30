

package org.springframework.scheduling.annotation;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import static org.junit.Assert.*;

/**
 * A {@link AsyncUncaughtExceptionHandler} implementation used for testing purposes.
 * @author Stephane Nicoll
 */
class TestableAsyncUncaughtExceptionHandler
		implements AsyncUncaughtExceptionHandler {

	private final CountDownLatch latch = new CountDownLatch(1);

	private UncaughtExceptionDescriptor descriptor;

	private final boolean throwUnexpectedException;

	TestableAsyncUncaughtExceptionHandler() {
		this(false);
	}

	TestableAsyncUncaughtExceptionHandler(boolean throwUnexpectedException) {
		this.throwUnexpectedException = throwUnexpectedException;
	}

	@Override
	public void handleUncaughtException(Throwable ex, Method method, Object... params) {
		descriptor = new UncaughtExceptionDescriptor(ex, method);
		this.latch.countDown();
		if (throwUnexpectedException) {
			throw new IllegalStateException("Test exception");
		}
	}

	public boolean isCalled() {
		return descriptor != null;
	}

	public void assertCalledWith(Method expectedMethod, Class<? extends Throwable> expectedExceptionType) {
		assertNotNull("Handler not called", descriptor);
		assertEquals("Wrong exception type", expectedExceptionType, descriptor.ex.getClass());
		assertEquals("Wrong method", expectedMethod, descriptor.method);
	}

	public void await(long timeout) {
		try {
			this.latch.await(timeout, TimeUnit.MILLISECONDS);
		}
		catch (Exception e) {
			Thread.currentThread().interrupt();
		}
	}

	private static class UncaughtExceptionDescriptor {
		private final Throwable ex;

		private final Method method;

		private UncaughtExceptionDescriptor(Throwable ex, Method method) {
			this.ex = ex;
			this.method = method;
		}
	}
}
