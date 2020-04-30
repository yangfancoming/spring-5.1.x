
package org.springframework.web.reactive.socket.server.support;

import reactor.core.publisher.Mono;

import org.springframework.util.Assert;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.HandlerAdapter;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.server.ServerWebExchange;

/**
 * {@link HandlerAdapter} that allows using a {@link WebSocketHandler} with the
 * generic {@link DispatcherHandler} mapping URLs directly to such handlers.
 * Requests are handled by delegating to the configured {@link WebSocketService}
 * which by default is {@link HandshakeWebSocketService}.
 *
 *
 * @since 5.0
 */
public class WebSocketHandlerAdapter implements HandlerAdapter {

	private final WebSocketService webSocketService;


	/**
	 * Default constructor that creates and uses a
	 * {@link HandshakeWebSocketService}.
	 */
	public WebSocketHandlerAdapter() {
		this(new HandshakeWebSocketService());
	}

	/**
	 * Alternative constructor with the {@link WebSocketService} to use.
	 */
	public WebSocketHandlerAdapter(WebSocketService webSocketService) {
		Assert.notNull(webSocketService, "'webSocketService' is required");
		this.webSocketService = webSocketService;
	}


	/**
	 * Return the configured {@code WebSocketService} to handle requests.
	 */
	public WebSocketService getWebSocketService() {
		return this.webSocketService;
	}


	@Override
	public boolean supports(Object handler) {
		return WebSocketHandler.class.isAssignableFrom(handler.getClass());
	}

	@Override
	public Mono<HandlerResult> handle(ServerWebExchange exchange, Object handler) {
		WebSocketHandler webSocketHandler = (WebSocketHandler) handler;
		return getWebSocketService().handleRequest(exchange, webSocketHandler).then(Mono.empty());
	}

}
