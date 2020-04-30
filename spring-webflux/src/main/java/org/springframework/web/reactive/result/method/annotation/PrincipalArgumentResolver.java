

package org.springframework.web.reactive.result.method.annotation;

import java.security.Principal;

import reactor.core.publisher.Mono;

import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolverSupport;
import org.springframework.web.server.ServerWebExchange;

/**
 * Resolves method argument value of type {@link java.security.Principal}.
 *
 *
 * @since 5.0
 * @see ServerWebExchangeArgumentResolver
 */
public class PrincipalArgumentResolver extends HandlerMethodArgumentResolverSupport {

	public PrincipalArgumentResolver(ReactiveAdapterRegistry adapterRegistry) {
		super(adapterRegistry);
	}


	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return checkParameterType(parameter, Principal.class::isAssignableFrom);
	}

	@Override
	public Mono<Object> resolveArgument(
			MethodParameter parameter, BindingContext context, ServerWebExchange exchange) {

		Mono<Principal> principal = exchange.getPrincipal();
		ReactiveAdapter adapter = getAdapterRegistry().getAdapter(parameter.getParameterType());
		return (adapter != null ? Mono.just(adapter.fromPublisher(principal)) : Mono.from(principal));
	}

}
