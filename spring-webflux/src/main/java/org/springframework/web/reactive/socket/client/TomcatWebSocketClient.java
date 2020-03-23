

package org.springframework.web.reactive.socket.client;

import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.apache.tomcat.websocket.WsWebSocketContainer;
import reactor.core.publisher.MonoProcessor;

import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.adapter.StandardWebSocketSession;
import org.springframework.web.reactive.socket.adapter.TomcatWebSocketSession;

/**
 * {@link WebSocketClient} implementation for use with the Java WebSocket API.
 *
 * @author Violeta Georgieva
 * @since 5.0
 */
public class TomcatWebSocketClient extends StandardWebSocketClient {


	public TomcatWebSocketClient() {
		this(new WsWebSocketContainer());
	}

	public TomcatWebSocketClient(WebSocketContainer webSocketContainer) {
		super(webSocketContainer);
	}


	@Override
	protected StandardWebSocketSession createWebSocketSession(Session session,
			HandshakeInfo info, MonoProcessor<Void> completion) {

			return new TomcatWebSocketSession(session, info, bufferFactory(), completion);
	}

}
