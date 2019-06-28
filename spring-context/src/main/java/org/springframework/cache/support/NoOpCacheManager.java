

package org.springframework.cache.support;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;

/**
 * A basic, no operation {@link CacheManager} implementation suitable
 * for disabling caching, typically used for backing cache declarations
 * without an actual backing store.
 *
 * <p>Will simply accept any items into the cache not actually storing them.
 *
 * @author Costin Leau
 * @author Stephane Nicoll
 * @since 3.1
 * @see CompositeCacheManager
 */
public class NoOpCacheManager implements CacheManager {

	private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<>(16);

	private final Set<String> cacheNames = new LinkedHashSet<>(16);


	/**
	 * This implementation always returns a {@link Cache} implementation that will not store items.
	 * Additionally, the request cache will be remembered by the manager for consistency.
	 */
	@Override
	@Nullable
	public Cache getCache(String name) {
		Cache cache = this.caches.get(name);
		if (cache == null) {
			this.caches.computeIfAbsent(name, key -> new NoOpCache(name));
			synchronized (this.cacheNames) {
				this.cacheNames.add(name);
			}
		}

		return this.caches.get(name);
	}

	/**
	 * This implementation returns the name of the caches previously requested.
	 */
	@Override
	public Collection<String> getCacheNames() {
		synchronized (this.cacheNames) {
			return Collections.unmodifiableSet(this.cacheNames);
		}
	}

}
