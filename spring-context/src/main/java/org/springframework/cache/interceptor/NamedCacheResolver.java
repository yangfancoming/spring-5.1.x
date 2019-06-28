

package org.springframework.cache.interceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;

/**
 * A {@link CacheResolver} that forces the resolution to a configurable
 * collection of name(s) against a given {@link CacheManager}.
 *
 * @author Stephane Nicoll
 * @since 4.1
 */
public class NamedCacheResolver extends AbstractCacheResolver {

	@Nullable
	private Collection<String> cacheNames;


	public NamedCacheResolver() {
	}

	public NamedCacheResolver(CacheManager cacheManager, String... cacheNames) {
		super(cacheManager);
		this.cacheNames = new ArrayList<>(Arrays.asList(cacheNames));
	}


	/**
	 * Set the cache name(s) that this resolver should use.
	 */
	public void setCacheNames(Collection<String> cacheNames) {
		this.cacheNames = cacheNames;
	}

	@Override
	protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {
		return this.cacheNames;
	}

}
