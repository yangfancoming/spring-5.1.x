

package org.springframework.web.context.request.async;

import java.util.function.Consumer;

import org.junit.Test;

import org.springframework.web.context.request.async.DeferredResult.DeferredResultHandler;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * DeferredResult tests.
 *
 * @author Rossen Stoyanchev
 */
public class DeferredResultTests {

	@Test
	public void setResult() {
		DeferredResultHandler handler = mock(DeferredResultHandler.class);

		DeferredResult<String> result = new DeferredResult<>();
		result.setResultHandler(handler);

		assertTrue(result.setResult("hello"));
		verify(handler).handleResult("hello");
	}

	@Test
	public void setResultTwice() {
		DeferredResultHandler handler = mock(DeferredResultHandler.class);

		DeferredResult<String> result = new DeferredResult<>();
		result.setResultHandler(handler);

		assertTrue(result.setResult("hello"));
		assertFalse(result.setResult("hi"));

		verify(handler).handleResult("hello");
	}

	@Test
	public void isSetOrExpired() {
		DeferredResultHandler handler = mock(DeferredResultHandler.class);

		DeferredResult<String> result = new DeferredResult<>();
		result.setResultHandler(handler);

		assertFalse(result.isSetOrExpired());

		result.setResult("hello");

		assertTrue(result.isSetOrExpired());

		verify(handler).handleResult("hello");
	}

	@Test
	public void hasResult() {
		DeferredResultHandler handler = mock(DeferredResultHandler.class);

		DeferredResult<String> result = new DeferredResult<>();
		result.setResultHandler(handler);

		assertFalse(result.hasResult());
		assertNull(result.getResult());

		result.setResult("hello");

		assertEquals("hello", result.getResult());
	}

	@Test
	public void onCompletion() throws Exception {
		final StringBuilder sb = new StringBuilder();

		DeferredResult<String> result = new DeferredResult<>();
		result.onCompletion(new Runnable() {
			@Override
			public void run() {
				sb.append("completion event");
			}
		});

		result.getInterceptor().afterCompletion(null, null);

		assertTrue(result.isSetOrExpired());
		assertEquals("completion event", sb.toString());
	}

	@Test
	public void onTimeout() throws Exception {
		final StringBuilder sb = new StringBuilder();

		DeferredResultHandler handler = mock(DeferredResultHandler.class);

		DeferredResult<String> result = new DeferredResult<>(null, "timeout result");
		result.setResultHandler(handler);
		result.onTimeout(new Runnable() {
			@Override
			public void run() {
				sb.append("timeout event");
			}
		});

		result.getInterceptor().handleTimeout(null, null);

		assertEquals("timeout event", sb.toString());
		assertFalse("Should not be able to set result a second time", result.setResult("hello"));
		verify(handler).handleResult("timeout result");
	}

	@Test
	public void onError() throws Exception {
		final StringBuilder sb = new StringBuilder();

		DeferredResultHandler handler = mock(DeferredResultHandler.class);

		DeferredResult<String> result = new DeferredResult<>(null, "error result");
		result.setResultHandler(handler);
		Exception e = new Exception();
		result.onError(new Consumer<Throwable>() {
			@Override
			public void accept(Throwable t) {
				sb.append("error event");
			}
		});

		result.getInterceptor().handleError(null, null, e);

		assertEquals("error event", sb.toString());
		assertFalse("Should not be able to set result a second time", result.setResult("hello"));
		verify(handler).handleResult(e);
	}

}
