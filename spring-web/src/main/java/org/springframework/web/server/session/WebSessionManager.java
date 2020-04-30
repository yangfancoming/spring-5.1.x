

package org.springframework.web.server.session;

import reactor.core.publisher.Mono;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;

/**
 * Main class for for access to the {@link WebSession} for an HTTP request.
 *
 *
 * @since 5.0
 * @see WebSessionIdResolver
 * @see WebSessionStore
 */
public interface WebSessionManager {

	/**
	 * Return the {@link WebSession} for the given exchange. Always guaranteed
	 * to return an instance either matching to the session id requested by the
	 * client, or a new session either because the client did not specify one
	 * or because the underlying session expired.
	 * @param exchange the current exchange
	 * @return promise for the WebSession
	 */
	Mono<WebSession> getSession(ServerWebExchange exchange);

}
