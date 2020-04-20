
package org.springframework.util.concurrent;

import java.time.Duration;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;
import reactor.core.publisher.Mono;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link MonoToListenableFutureAdapter}.
 */
public class MonoToListenableFutureAdapterTests {

	@Test
	public void success() {
		String expected = "one";
		AtomicReference<Object> actual = new AtomicReference<>();
		ListenableFuture<String> future = new MonoToListenableFutureAdapter<>(Mono.just(expected));
		future.addCallback(actual::set, actual::set);
		assertEquals(expected, actual.get());
	}

	@Test
	public void failure() {
		Throwable expected = new IllegalStateException("oops");
		AtomicReference<Object> actual = new AtomicReference<>();
		ListenableFuture<String> future = new MonoToListenableFutureAdapter<>(Mono.error(expected));
		future.addCallback(actual::set, actual::set);
		assertEquals(expected, actual.get());
	}

	@Test
	public void cancellation() {
		Mono<Long> mono = Mono.delay(Duration.ofSeconds(60));
		Future<Long> future = new MonoToListenableFutureAdapter<>(mono);
		assertTrue(future.cancel(true));
		assertTrue(future.isCancelled());
	}

	@Test
	public void cancellationAfterTerminated() {
		Future<Void> future = new MonoToListenableFutureAdapter<>(Mono.empty());
		assertFalse("Should return false if task already completed", future.cancel(true));
		assertFalse(future.isCancelled());
	}

}
