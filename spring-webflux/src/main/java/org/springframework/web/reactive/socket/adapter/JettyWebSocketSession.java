

package org.springframework.web.reactive.socket.adapter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.SuspendToken;
import org.eclipse.jetty.websocket.api.WriteCallback;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;

import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

/**
 * Spring {@link WebSocketSession} implementation that adapts to a Jetty
 * WebSocket {@link org.eclipse.jetty.websocket.api.Session}.
 *
 * @author Violeta Georgieva
 *
 * @since 5.0
 */
public class JettyWebSocketSession extends AbstractListenerWebSocketSession<Session> {

	@Nullable
	private volatile SuspendToken suspendToken;


	public JettyWebSocketSession(Session session, HandshakeInfo info, DataBufferFactory factory) {
		this(session, info, factory, null);
	}

	public JettyWebSocketSession(Session session, HandshakeInfo info, DataBufferFactory factory,
			@Nullable MonoProcessor<Void> completionMono) {

		super(session, ObjectUtils.getIdentityHexString(session), info, factory, completionMono);
		// TODO: suspend causes failures if invoked at this stage
		// suspendReceiving();
	}


	@Override
	protected boolean canSuspendReceiving() {
		return true;
	}

	@Override
	protected void suspendReceiving() {
		Assert.state(this.suspendToken == null, "Already suspended");
		this.suspendToken = getDelegate().suspend();
	}

	@Override
	protected void resumeReceiving() {
		SuspendToken tokenToUse = this.suspendToken;
		this.suspendToken = null;
		if (tokenToUse != null) {
			tokenToUse.resume();
		}
	}

	@Override
	protected boolean sendMessage(WebSocketMessage message) throws IOException {
		ByteBuffer buffer = message.getPayload().asByteBuffer();
		if (WebSocketMessage.Type.TEXT.equals(message.getType())) {
			getSendProcessor().setReadyToSend(false);
			String text = new String(buffer.array(), StandardCharsets.UTF_8);
			getDelegate().getRemote().sendString(text, new SendProcessorCallback());
		}
		else if (WebSocketMessage.Type.BINARY.equals(message.getType())) {
			getSendProcessor().setReadyToSend(false);
			getDelegate().getRemote().sendBytes(buffer, new SendProcessorCallback());
		}
		else if (WebSocketMessage.Type.PING.equals(message.getType())) {
			getDelegate().getRemote().sendPing(buffer);
		}
		else if (WebSocketMessage.Type.PONG.equals(message.getType())) {
			getDelegate().getRemote().sendPong(buffer);
		}
		else {
			throw new IllegalArgumentException("Unexpected message type: " + message.getType());
		}
		return true;
	}

	@Override
	public Mono<Void> close(CloseStatus status) {
		getDelegate().close(status.getCode(), status.getReason());
		return Mono.empty();
	}


	private final class SendProcessorCallback implements WriteCallback {

		@Override
		public void writeFailed(Throwable x) {
			getSendProcessor().cancel();
			getSendProcessor().onError(x);
		}

		@Override
		public void writeSuccess() {
			getSendProcessor().setReadyToSend(true);
			getSendProcessor().onWritePossible();
		}

	}

}
