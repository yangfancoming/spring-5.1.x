

package org.springframework.web.server.session;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;

/**
 * Request and response header-based {@link WebSessionIdResolver}.
 *
 * @author Greg Turnquist
 * @author Rob Winch
 * @since 5.0
 */
public class HeaderWebSessionIdResolver implements WebSessionIdResolver {

	/** Default value for {@link #setHeaderName(String)}. */
	public static final String DEFAULT_HEADER_NAME = "SESSION";


	private String headerName = DEFAULT_HEADER_NAME;


	/**
	 * Set the name of the session header to use for the session id.
	 * The name is used to extract the session id from the request headers as
	 * well to set the session id on the response headers.
	 * <p>By default set to {@code DEFAULT_HEADER_NAME}
	 * @param headerName the header name
	 */
	public void setHeaderName(String headerName) {
		Assert.hasText(headerName, "'headerName' must not be empty");
		this.headerName = headerName;
	}

	/**
	 * Return the configured header name.
	 * @return the configured header name
	 */
	public String getHeaderName() {
		return this.headerName;
	}


	@Override
	public List<String> resolveSessionIds(ServerWebExchange exchange) {
		HttpHeaders headers = exchange.getRequest().getHeaders();
		return headers.getOrDefault(getHeaderName(), Collections.emptyList());
	}

	@Override
	public void setSessionId(ServerWebExchange exchange, String id) {
		Assert.notNull(id, "'id' is required.");
		exchange.getResponse().getHeaders().set(getHeaderName(), id);
	}

	@Override
	public void expireSession(ServerWebExchange exchange) {
		this.setSessionId(exchange, "");
	}

}
