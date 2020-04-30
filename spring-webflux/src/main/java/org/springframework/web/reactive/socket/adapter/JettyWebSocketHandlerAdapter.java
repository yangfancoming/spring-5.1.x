

package org.springframework.web.reactive.socket.adapter;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketFrame;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.extensions.Frame;
import org.eclipse.jetty.websocket.common.OpCode;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketMessage.Type;
import org.springframework.web.reactive.socket.WebSocketSession;

/**
 * Jetty {@link WebSocket @WebSocket} handler that delegates events to a
 * reactive {@link WebSocketHandler} and its session.
 *
 * @author Violeta Georgieva
 *
 * @since 5.0
 */
@WebSocket
public class JettyWebSocketHandlerAdapter {

	private static final ByteBuffer EMPTY_PAYLOAD = ByteBuffer.wrap(new byte[0]);


	private final WebSocketHandler delegateHandler;

	private final Function<Session, JettyWebSocketSession> sessionFactory;

	@Nullable
	private JettyWebSocketSession delegateSession;


	public JettyWebSocketHandlerAdapter(WebSocketHandler handler,
			Function<Session, JettyWebSocketSession> sessionFactory) {

		Assert.notNull(handler, "WebSocketHandler is required");
		Assert.notNull(sessionFactory, "'sessionFactory' is required");
		this.delegateHandler = handler;
		this.sessionFactory = sessionFactory;
	}


	@OnWebSocketConnect
	public void onWebSocketConnect(Session session) {
		this.delegateSession = this.sessionFactory.apply(session);
		this.delegateHandler.handle(this.delegateSession).subscribe(this.delegateSession);
	}

	@OnWebSocketMessage
	public void onWebSocketText(String message) {
		if (this.delegateSession != null) {
			WebSocketMessage webSocketMessage = toMessage(Type.TEXT, message);
			this.delegateSession.handleMessage(webSocketMessage.getType(), webSocketMessage);
		}
	}

	@OnWebSocketMessage
	public void onWebSocketBinary(byte[] message, int offset, int length) {
		if (this.delegateSession != null) {
			ByteBuffer buffer = ByteBuffer.wrap(message, offset, length);
			WebSocketMessage webSocketMessage = toMessage(Type.BINARY, buffer);
			this.delegateSession.handleMessage(webSocketMessage.getType(), webSocketMessage);
		}
	}

	@OnWebSocketFrame
	public void onWebSocketFrame(Frame frame) {
		if (this.delegateSession != null) {
			if (OpCode.PONG == frame.getOpCode()) {
				ByteBuffer buffer = (frame.getPayload() != null ? frame.getPayload() : EMPTY_PAYLOAD);
				WebSocketMessage webSocketMessage = toMessage(Type.PONG, buffer);
				this.delegateSession.handleMessage(webSocketMessage.getType(), webSocketMessage);
			}
		}
	}

	private <T> WebSocketMessage toMessage(Type type, T message) {
		WebSocketSession session = this.delegateSession;
		Assert.state(session != null, "Cannot create message without a session");
		if (Type.TEXT.equals(type)) {
			byte[] bytes = ((String) message).getBytes(StandardCharsets.UTF_8);
			DataBuffer buffer = session.bufferFactory().wrap(bytes);
			return new WebSocketMessage(Type.TEXT, buffer);
		}
		else if (Type.BINARY.equals(type)) {
			DataBuffer buffer = session.bufferFactory().wrap((ByteBuffer) message);
			return new WebSocketMessage(Type.BINARY, buffer);
		}
		else if (Type.PONG.equals(type)) {
			DataBuffer buffer = session.bufferFactory().wrap((ByteBuffer) message);
			return new WebSocketMessage(Type.PONG, buffer);
		}
		else {
			throw new IllegalArgumentException("Unexpected message type: " + message);
		}
	}

	@OnWebSocketClose
	public void onWebSocketClose(int statusCode, String reason) {
		if (this.delegateSession != null) {
			this.delegateSession.handleClose(new CloseStatus(statusCode, reason));
		}
	}

	@OnWebSocketError
	public void onWebSocketError(Throwable cause) {
		if (this.delegateSession != null) {
			this.delegateSession.handleError(cause);
		}
	}

}
