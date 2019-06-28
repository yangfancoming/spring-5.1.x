

package org.springframework.http.server.reactive;

import org.junit.Before;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import org.springframework.core.io.buffer.DataBuffer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AbstractListenerReadPublisher}.
 *
 * @author Violeta Georgieva
 * @author Rossen Stoyanchev
 */
public class ListenerReadPublisherTests {

	private final TestListenerReadPublisher publisher = new TestListenerReadPublisher();

	private final TestSubscriber subscriber = new TestSubscriber();


	@Before
	public void setup() {
		this.publisher.subscribe(this.subscriber);
	}


	@Test
	public void twoReads() {

		this.subscriber.getSubscription().request(2);
		this.publisher.onDataAvailable();

		assertEquals(2, this.publisher.getReadCalls());
	}

	@Test // SPR-17410
	public void discardDataOnError() {

		this.subscriber.getSubscription().request(2);
		this.publisher.onDataAvailable();
		this.publisher.onError(new IllegalStateException());

		assertEquals(2, this.publisher.getReadCalls());
		assertEquals(1, this.publisher.getDiscardCalls());
	}

	@Test // SPR-17410
	public void discardDataOnCancel() {

		this.subscriber.getSubscription().request(2);
		this.subscriber.setCancelOnNext(true);
		this.publisher.onDataAvailable();

		assertEquals(1, this.publisher.getReadCalls());
		assertEquals(1, this.publisher.getDiscardCalls());
	}


	private static final class TestListenerReadPublisher extends AbstractListenerReadPublisher<DataBuffer> {

		private int readCalls = 0;

		private int discardCalls = 0;


		public TestListenerReadPublisher() {
			super("");
		}


		public int getReadCalls() {
			return this.readCalls;
		}

		public int getDiscardCalls() {
			return this.discardCalls;
		}

		@Override
		protected void checkOnDataAvailable() {
			// no-op
		}

		@Override
		protected DataBuffer read() {
			this.readCalls++;
			return mock(DataBuffer.class);
		}

		@Override
		protected void readingPaused() {
			// No-op
		}

		@Override
		protected void discardData() {
			this.discardCalls++;
		}
	}


	private static final class TestSubscriber implements Subscriber<DataBuffer> {

		private Subscription subscription;

		private boolean cancelOnNext;


		public Subscription getSubscription() {
			return this.subscription;
		}

		public void setCancelOnNext(boolean cancelOnNext) {
			this.cancelOnNext = cancelOnNext;
		}


		@Override
		public void onSubscribe(Subscription subscription) {
			this.subscription = subscription;
		}

		@Override
		public void onNext(DataBuffer dataBuffer) {
			if (this.cancelOnNext) {
				this.subscription.cancel();
			}
		}

		@Override
		public void onError(Throwable t) {
		}

		@Override
		public void onComplete() {
		}
	}

}
