

package org.springframework.web.socket.sockjs.transport.handler;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.sockjs.frame.DefaultSockJsFrameFormat;
import org.springframework.web.socket.sockjs.frame.SockJsFrameFormat;
import org.springframework.web.socket.sockjs.transport.SockJsServiceConfig;
import org.springframework.web.socket.sockjs.transport.SockJsSession;
import org.springframework.web.socket.sockjs.transport.TransportType;
import org.springframework.web.socket.sockjs.transport.session.StreamingSockJsSession;

/**
 * A TransportHandler for sending messages via Server-Sent events:
 * <a href="https://dev.w3.org/html5/eventsource/">https://dev.w3.org/html5/eventsource/</a>.
 *
 *
 * @since 4.0
 */
public class EventSourceTransportHandler extends AbstractHttpSendingTransportHandler {

	@Override
	public TransportType getTransportType() {
		return TransportType.EVENT_SOURCE;
	}

	@Override
	protected MediaType getContentType() {
		return new MediaType("text", "event-stream", StandardCharsets.UTF_8);
	}

	@Override
	public boolean checkSessionType(SockJsSession session) {
		return session instanceof EventSourceStreamingSockJsSession;
	}

	@Override
	public StreamingSockJsSession createSession(
			String sessionId, WebSocketHandler handler, Map<String, Object> attributes) {

		return new EventSourceStreamingSockJsSession(sessionId, getServiceConfig(), handler, attributes);
	}

	@Override
	protected SockJsFrameFormat getFrameFormat(ServerHttpRequest request) {
		return new DefaultSockJsFrameFormat("data: %s\r\n\r\n");
	}


	private class EventSourceStreamingSockJsSession extends StreamingSockJsSession {

		public EventSourceStreamingSockJsSession(String sessionId, SockJsServiceConfig config,
				WebSocketHandler wsHandler, Map<String, Object> attributes) {

			super(sessionId, config, wsHandler, attributes);
		}

		@Override
		protected byte[] getPrelude(ServerHttpRequest request) {
			return new byte[] { '\r', '\n' };
		}
	}

}
