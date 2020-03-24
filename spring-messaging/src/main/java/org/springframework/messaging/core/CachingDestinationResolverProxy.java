

package org.springframework.messaging.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * {@link DestinationResolver} implementation that proxies a target DestinationResolver,
 * caching its {@link #resolveDestination} results. Such caching is particularly useful
 * if the destination resolving process is expensive (e.g. the destination has to be
 * resolved through an external system) and the resolution results are stable anyway.
 *
 * @author Agim Emruli

 * @since 4.1
 * @param <D> the destination type
 * @see DestinationResolver#resolveDestination
 */
public class CachingDestinationResolverProxy<D> implements DestinationResolver<D>, InitializingBean {

	private final Map<String, D> resolvedDestinationCache = new ConcurrentHashMap<>();

	@Nullable
	private DestinationResolver<D> targetDestinationResolver;


	/**
	 * Create a new CachingDestinationResolverProxy, setting the target DestinationResolver
	 * through the {@link #setTargetDestinationResolver} bean property.
	 */
	public CachingDestinationResolverProxy() {
	}

	/**
	 * Create a new CachingDestinationResolverProxy using the given target
	 * DestinationResolver to actually resolve destinations.
	 * @param targetDestinationResolver the target DestinationResolver to delegate to
	 */
	public CachingDestinationResolverProxy(DestinationResolver<D> targetDestinationResolver) {
		Assert.notNull(targetDestinationResolver, "Target DestinationResolver must not be null");
		this.targetDestinationResolver = targetDestinationResolver;
	}


	/**
	 * Set the target DestinationResolver to delegate to.
	 */
	public void setTargetDestinationResolver(DestinationResolver<D> targetDestinationResolver) {
		this.targetDestinationResolver = targetDestinationResolver;
	}

	@Override
	public void afterPropertiesSet() {
		if (this.targetDestinationResolver == null) {
			throw new IllegalArgumentException("Property 'targetDestinationResolver' is required");
		}
	}


	/**
	 * Resolves and caches destinations if successfully resolved by the target
	 * DestinationResolver implementation.
	 * @param name the destination name to be resolved
	 * @return the currently resolved destination or an already cached destination
	 * @throws DestinationResolutionException if the target DestinationResolver
	 * reports an error during destination resolution
	 */
	@Override
	public D resolveDestination(String name) throws DestinationResolutionException {
		D destination = this.resolvedDestinationCache.get(name);
		if (destination == null) {
			Assert.state(this.targetDestinationResolver != null, "No target DestinationResolver set");
			destination = this.targetDestinationResolver.resolveDestination(name);
			this.resolvedDestinationCache.put(name, destination);
		}
		return destination;
	}

}
