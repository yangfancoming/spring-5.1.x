

package org.springframework.util.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;


public class FutureAdapterTests {

	private FutureAdapter<String, Integer> adapter;

	private Future<Integer> adaptee;


	@Before
	@SuppressWarnings("unchecked")
	public void setUp() {
		adaptee = mock(Future.class);
		adapter = new FutureAdapter<String, Integer>(adaptee) {
			@Override
			protected String adapt(Integer adapteeResult) throws ExecutionException {
				return adapteeResult.toString();
			}
		};
	}

	@Test
	public void cancel() throws Exception {
		given(adaptee.cancel(true)).willReturn(true);
		boolean result = adapter.cancel(true);
		assertTrue(result);
	}

	@Test
	public void isCancelled() {
		given(adaptee.isCancelled()).willReturn(true);
		boolean result = adapter.isCancelled();
		assertTrue(result);
	}

	@Test
	public void isDone() {
		given(adaptee.isDone()).willReturn(true);
		boolean result = adapter.isDone();
		assertTrue(result);
	}

	@Test
	public void get() throws Exception {
		given(adaptee.get()).willReturn(42);
		String result = adapter.get();
		assertEquals("42", result);
	}

	@Test
	public void getTimeOut() throws Exception {
		given(adaptee.get(1, TimeUnit.SECONDS)).willReturn(42);
		String result = adapter.get(1, TimeUnit.SECONDS);
		assertEquals("42", result);
	}


}
