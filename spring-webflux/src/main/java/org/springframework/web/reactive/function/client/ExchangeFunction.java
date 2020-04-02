

package org.springframework.web.reactive.function.client;

import reactor.core.publisher.Mono;

/**
 * Represents a function that exchanges a {@linkplain ClientRequest request} for a (delayed)
 * {@linkplain ClientResponse}. Can be used as an alternative to {@link WebClient}.
 *
 * For example:
 * <pre class="code">
 * ExchangeFunction exchangeFunction = ExchangeFunctions.create(new ReactorClientHttpConnector());
 * ClientRequest&lt;Void&gt; request = ClientRequest.method(HttpMethod.GET, "https://example.com/resource").build();
 *
 * Mono&lt;String&gt; result = exchangeFunction
 *     .exchange(request)
 *     .then(response -> response.bodyToMono(String.class));
 * </pre>
 *
 * @author Arjen Poutsma
 * @since 5.0
 */
@FunctionalInterface
public interface ExchangeFunction {

	/**
	 * Exchange the given request for a response mono.
	 * @param request the request to exchange
	 * @return the delayed response
	 */
	Mono<ClientResponse> exchange(ClientRequest request);

	/**
	 * Filters this exchange function with the given {@code ExchangeFilterFunction}, resulting in a
	 * filtered {@code ExchangeFunction}.
	 * @param filter the filter to apply to this exchange
	 * @return the filtered exchange
	 * @see ExchangeFilterFunction#apply(ExchangeFunction)
	 */
	default ExchangeFunction filter(ExchangeFilterFunction filter) {
		return filter.apply(this);
	}

}
