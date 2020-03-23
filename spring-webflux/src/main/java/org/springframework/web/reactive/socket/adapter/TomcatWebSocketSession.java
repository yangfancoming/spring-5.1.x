

package org.springframework.web.reactive.socket.adapter;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import javax.websocket.Session;

import org.apache.tomcat.websocket.WsSession;
import reactor.core.publisher.MonoProcessor;

import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketSession;

/**
 * Spring {@link WebSocketSession} adapter for Tomcat's
 * {@link javax.websocket.Session}.
 *
 * @author Violeta Georgieva
 * @since 5.0
 */
public class TomcatWebSocketSession extends StandardWebSocketSession {

	private static final AtomicIntegerFieldUpdater<TomcatWebSocketSession> SUSPENDED =
			AtomicIntegerFieldUpdater.newUpdater(TomcatWebSocketSession.class, "suspended");

	@SuppressWarnings("unused")
	private volatile int suspended;


	public TomcatWebSocketSession(Session session, HandshakeInfo info, DataBufferFactory factory) {
		super(session, info, factory);
	}

	public TomcatWebSocketSession(Session session, HandshakeInfo info, DataBufferFactory factory,
			MonoProcessor<Void> completionMono) {

		super(session, info, factory, completionMono);
		suspendReceiving();
	}


	@Override
	protected boolean canSuspendReceiving() {
		return true;
	}

	@Override
	protected void suspendReceiving() {
		if (SUSPENDED.compareAndSet(this, 0, 1)) {
			((WsSession) getDelegate()).suspend();
		}
	}

	@Override
	protected void resumeReceiving() {
		if (SUSPENDED.compareAndSet(this, 1, 0)) {
			((WsSession) getDelegate()).resume();
		}
	}

}
