

package org.springframework.web.reactive.socket.server;

import java.util.function.Supplier;

import reactor.core.publisher.Mono;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.server.ServerWebExchange;

/**
 * A strategy for upgrading an HTTP request to a WebSocket session depending
 * on the underlying network runtime.
 *
 * Typically there is one such strategy for every {@link ServerHttpRequest}
 * and {@link ServerHttpResponse} type except in the case of Servlet containers
 * for which the standard Java WebSocket API JSR-356 does not define a way to
 * upgrade a request so a custom strategy is needed for every Servlet container.
 *
 *
 * @since 5.0
 */
public interface RequestUpgradeStrategy {

	/**
	 * Upgrade to a WebSocket session and handle it with the given handler.
	 * @param exchange the current exchange
	 * @param webSocketHandler handler for the WebSocket session
	 * @param subProtocol the selected sub-protocol got the handler
	 * @return completion {@code Mono<Void>} to indicate the outcome of the
	 * WebSocket session handling.
	 * @deprecated as of 5.1 in favor of
	 * {@link #upgrade(ServerWebExchange, WebSocketHandler, String, Supplier)}
	 */
	@Deprecated
	default Mono<Void> upgrade(ServerWebExchange exchange, WebSocketHandler webSocketHandler,
			@Nullable String subProtocol) {

		return Mono.error(new UnsupportedOperationException());
	}

	/**
	 * Upgrade to a WebSocket session and handle it with the given handler.
	 * @param exchange the current exchange
	 * @param webSocketHandler handler for the WebSocket session
	 * @param subProtocol the selected sub-protocol got the handler
	 * @param handshakeInfoFactory factory to create HandshakeInfo for the WebSocket session
	 * @return completion {@code Mono<Void>} to indicate the outcome of the
	 * WebSocket session handling.
	 * @since 5.1
	 */
	@SuppressWarnings("deprecation")
	default Mono<Void> upgrade(ServerWebExchange exchange, WebSocketHandler webSocketHandler,
			@Nullable String subProtocol, Supplier<HandshakeInfo> handshakeInfoFactory) {

		return upgrade(exchange, webSocketHandler, subProtocol);
	}

}
