

package org.springframework.web.filter.reactive;

import reactor.core.publisher.Mono;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.adapter.ForwardedHeaderTransformer;

/**
 * Extract values from "Forwarded" and "X-Forwarded-*" headers to override the
 * request URI (i.e. {@link ServerHttpRequest#getURI()}) so it reflects the
 * client-originated protocol and address.
 *
 * Alternatively if {@link #setRemoveOnly removeOnly} is set to "true", then
 * "Forwarded" and "X-Forwarded-*" headers are only removed, and not used.
 *
 * @author Arjen Poutsma
 *
 * @deprecated as of 5.1 this filter is deprecated in favor of using
 * {@link ForwardedHeaderTransformer} which can be declared as a bean with the
 * name "forwardedHeaderTransformer" or registered explicitly in
 * {@link org.springframework.web.server.adapter.WebHttpHandlerBuilder
 * WebHttpHandlerBuilder}.
 * @since 5.0
 * @see <a href="https://tools.ietf.org/html/rfc7239">https://tools.ietf.org/html/rfc7239</a>
 */
@Deprecated
public class ForwardedHeaderFilter extends ForwardedHeaderTransformer implements WebFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		if (hasForwardedHeaders(request)) {
			exchange = exchange.mutate().request(apply(request)).build();
		}
		return chain.filter(exchange);
	}

}
