

package org.springframework.web.socket.config.annotation;

import org.springframework.web.socket.WebSocketHandler;

/**
 * Defines callback methods to configure the WebSocket request handling
 * via {@link org.springframework.web.socket.config.annotation.EnableWebSocket @EnableWebSocket}.
 *
 *
 * @since 4.0
 */
public interface WebSocketConfigurer {

	/**
	 * Register {@link WebSocketHandler WebSocketHandlers} including SockJS fallback options if desired.
	 */
	void registerWebSocketHandlers(WebSocketHandlerRegistry registry);

}
