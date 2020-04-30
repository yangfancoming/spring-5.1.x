

package org.springframework.web.reactive;

import reactor.core.publisher.Mono;

import org.springframework.web.server.ServerWebExchange;

/**
 * Process the {@link HandlerResult}, usually returned by an {@link HandlerAdapter}.
 *
 *
 * @author Sebastien Deleuze
 * @since 5.0
 */
public interface HandlerResultHandler {

	/**
	 * Whether this handler supports the given {@link HandlerResult}.
	 * @param result result object to check
	 * @return whether or not this object can use the given result
	 */
	boolean supports(HandlerResult result);

	/**
	 * Process the given result modifying response headers and/or writing data
	 * to the response.
	 * @param exchange current server exchange
	 * @param result the result from the handling
	 * @return {@code Mono<Void>} to indicate when request handling is complete.
	 */
	Mono<Void> handleResult(ServerWebExchange exchange, HandlerResult result);

}
