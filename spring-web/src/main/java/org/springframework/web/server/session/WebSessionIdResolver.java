

package org.springframework.web.server.session;

import java.util.List;

import org.springframework.web.server.ServerWebExchange;

/**
 * Contract for session id resolution strategies. Allows for session id
 * resolution through the request and for sending the session id or expiring
 * the session through the response.
 *
 *
 * @since 5.0
 * @see CookieWebSessionIdResolver
 */
public interface WebSessionIdResolver {

	/**
	 * Resolve the session id's associated with the request.
	 * @param exchange the current exchange
	 * @return the session id's or an empty list
	 */
	List<String> resolveSessionIds(ServerWebExchange exchange);

	/**
	 * Send the given session id to the client.
	 * @param exchange the current exchange
	 * @param sessionId the session id
	 */
	void setSessionId(ServerWebExchange exchange, String sessionId);

	/**
	 * Instruct the client to end the current session.
	 * @param exchange the current exchange
	 */
	void expireSession(ServerWebExchange exchange);

}
