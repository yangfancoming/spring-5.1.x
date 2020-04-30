

package org.springframework.web.socket.client;

import java.net.URI;

import org.springframework.lang.Nullable;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;

/**
 * Contract for initiating a WebSocket request. As an alternative considering using the
 * declarative style {@link WebSocketConnectionManager} that starts a WebSocket connection
 * to a pre-configured URI when the application starts.
 *
 *
 * @since 4.0
 * @see WebSocketConnectionManager
 */
public interface WebSocketClient {

	ListenableFuture<WebSocketSession> doHandshake(WebSocketHandler webSocketHandler,
			String uriTemplate, Object... uriVariables);

	ListenableFuture<WebSocketSession> doHandshake(WebSocketHandler webSocketHandler,
			@Nullable WebSocketHttpHeaders headers, URI uri);

}
