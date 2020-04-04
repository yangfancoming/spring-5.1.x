

package org.springframework.web.socket.sockjs.transport;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.sockjs.SockJsException;
import org.springframework.web.socket.sockjs.SockJsService;

/**
 * Handle a SockJS session URL, i.e. transport-specific request.
 *
 * @author Rossen Stoyanchev

 * @since 4.0
 */
public interface TransportHandler {

	/**
	 * Initialize this handler with the given configuration.
	 * @param serviceConfig the configuration as defined by the containing
	 * {@link org.springframework.web.socket.sockjs.SockJsService}
	 */
	void initialize(SockJsServiceConfig serviceConfig);

	/**
	 * Return the transport type supported by this handler.
	 */
	TransportType getTransportType();

	/**
	 * Check whether the type of the given session matches the transport type
	 * of this {@code TransportHandler} where session id and the transport type
	 * are extracted from the SockJS URL.
	 * @return {@code true} if the session matches (and would therefore get
	 * accepted by {@link #handleRequest}), or {@code false} otherwise
	 * @since 4.3.4
	 */
	boolean checkSessionType(SockJsSession session);

	/**
	 * Handle the given request and delegate messages to the provided
	 * {@link WebSocketHandler}.
	 * @param request the current request
	 * @param response the current response
	 * @param handler the target WebSocketHandler (never {@code null})
	 * @param session the SockJS session (never {@code null})
	 * @throws SockJsException raised when request processing fails as
	 * explained in {@link SockJsService}
	 */
	void handleRequest(ServerHttpRequest request, ServerHttpResponse response,
			WebSocketHandler handler, SockJsSession session) throws SockJsException;

}