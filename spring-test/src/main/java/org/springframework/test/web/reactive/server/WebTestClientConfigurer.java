

package org.springframework.test.web.reactive.server;

import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.lang.Nullable;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;

/**
 * Contract that frameworks or applications can use to pre-package a set of
 * customizations to a {@link WebTestClient.Builder} and expose that
 * as a shortcut.
 *
 *
 * @since 5.0
 * @see MockServerConfigurer
 */
public interface WebTestClientConfigurer {

	/**
	 * Invoked once only, immediately (i.e. before this method returns).
	 * @param builder the WebTestClient builder to make changes to
	 * @param httpHandlerBuilder the builder for the "mock server" HttpHandler
	 * this client was configured for "mock server" testing
	 * @param connector the connector for "live" integration tests if this
	 * server was configured for live integration testing
	 */
	void afterConfigurerAdded(WebTestClient.Builder builder,
			@Nullable WebHttpHandlerBuilder httpHandlerBuilder,
			@Nullable ClientHttpConnector connector);

}
