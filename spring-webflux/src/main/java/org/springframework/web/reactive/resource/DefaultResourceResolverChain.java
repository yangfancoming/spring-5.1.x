

package org.springframework.web.reactive.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import reactor.core.publisher.Mono;

import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;

/**
 * Default immutable implementation of {@link ResourceResolverChain}.
 *
 *
 * @since 5.0
 */
class DefaultResourceResolverChain implements ResourceResolverChain {

	@Nullable
	private final ResourceResolver resolver;

	@Nullable
	private final ResourceResolverChain nextChain;


	public DefaultResourceResolverChain(@Nullable List<? extends ResourceResolver> resolvers) {
		resolvers = (resolvers != null ? resolvers : Collections.emptyList());
		DefaultResourceResolverChain chain = initChain(new ArrayList<>(resolvers));
		this.resolver = chain.resolver;
		this.nextChain = chain.nextChain;
	}

	private static DefaultResourceResolverChain initChain(ArrayList<? extends ResourceResolver> resolvers) {
		DefaultResourceResolverChain chain = new DefaultResourceResolverChain(null, null);
		ListIterator<? extends ResourceResolver> it = resolvers.listIterator(resolvers.size());
		while (it.hasPrevious()) {
			chain = new DefaultResourceResolverChain(it.previous(), chain);
		}
		return chain;
	}

	private DefaultResourceResolverChain(@Nullable ResourceResolver resolver, @Nullable ResourceResolverChain chain) {
		Assert.isTrue((resolver == null && chain == null) || (resolver != null && chain != null),
				"Both resolver and resolver chain must be null, or neither is");
		this.resolver = resolver;
		this.nextChain = chain;
	}


	@Override
	public Mono<Resource> resolveResource(@Nullable ServerWebExchange exchange, String requestPath,
			List<? extends Resource> locations) {

		return (this.resolver != null && this.nextChain != null ?
				this.resolver.resolveResource(exchange, requestPath, locations, this.nextChain) :
				Mono.empty());
	}

	@Override
	public Mono<String> resolveUrlPath(String resourcePath, List<? extends Resource> locations) {
		return (this.resolver != null && this.nextChain != null ?
				this.resolver.resolveUrlPath(resourcePath, locations, this.nextChain) :
				Mono.empty());
	}

}
