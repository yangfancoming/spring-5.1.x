

package org.springframework.scheduling.annotation;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import static org.junit.Assert.*;


public class AsyncResultTests {

	@Test
	public void asyncResultWithCallbackAndValue() throws Exception {
		String value = "val";
		final Set<String> values = new HashSet<>(1);
		ListenableFuture<String> future = AsyncResult.forValue(value);
		future.addCallback(new ListenableFutureCallback<String>() {
			@Override
			public void onSuccess(String result) {
				values.add(result);
			}
			@Override
			public void onFailure(Throwable ex) {
				fail("Failure callback not expected: " + ex);
			}
		});
		assertSame(value, values.iterator().next());
		assertSame(value, future.get());
		assertSame(value, future.completable().get());
		future.completable().thenAccept(v -> assertSame(value, v));
	}

	@Test
	public void asyncResultWithCallbackAndException() throws Exception {
		IOException ex = new IOException();
		final Set<Throwable> values = new HashSet<>(1);
		ListenableFuture<String> future = AsyncResult.forExecutionException(ex);
		future.addCallback(new ListenableFutureCallback<String>() {
			@Override
			public void onSuccess(String result) {
				fail("Success callback not expected: " + result);
			}
			@Override
			public void onFailure(Throwable ex) {
				values.add(ex);
			}
		});
		assertSame(ex, values.iterator().next());
		try {
			future.get();
			fail("Should have thrown ExecutionException");
		}
		catch (ExecutionException ex2) {
			assertSame(ex, ex2.getCause());
		}
		try {
			future.completable().get();
			fail("Should have thrown ExecutionException");
		}
		catch (ExecutionException ex2) {
			assertSame(ex, ex2.getCause());
		}
	}

	@Test
	public void asyncResultWithSeparateCallbacksAndValue() throws Exception {
		String value = "val";
		final Set<String> values = new HashSet<>(1);
		ListenableFuture<String> future = AsyncResult.forValue(value);
		future.addCallback(values::add, (ex) -> fail("Failure callback not expected: " + ex));
		assertSame(value, values.iterator().next());
		assertSame(value, future.get());
		assertSame(value, future.completable().get());
		future.completable().thenAccept(v -> assertSame(value, v));
	}

	@Test
	public void asyncResultWithSeparateCallbacksAndException() throws Exception {
		IOException ex = new IOException();
		final Set<Throwable> values = new HashSet<>(1);
		ListenableFuture<String> future = AsyncResult.forExecutionException(ex);
		future.addCallback((result) -> fail("Success callback not expected: " + result), values::add);
		assertSame(ex, values.iterator().next());
		try {
			future.get();
			fail("Should have thrown ExecutionException");
		}
		catch (ExecutionException ex2) {
			assertSame(ex, ex2.getCause());
		}
		try {
			future.completable().get();
			fail("Should have thrown ExecutionException");
		}
		catch (ExecutionException ex2) {
			assertSame(ex, ex2.getCause());
		}
	}

}
