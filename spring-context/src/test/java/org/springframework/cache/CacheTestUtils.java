

package org.springframework.cache;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;

import static org.junit.Assert.*;

/**
 * General cache-related test utilities.
 */
public class CacheTestUtils {

	/**
	 * Create a {@link SimpleCacheManager} with the specified cache(s).
	 * @param cacheNames the names of the caches to create
	 */
	public static CacheManager createSimpleCacheManager(String... cacheNames) {
		SimpleCacheManager result = new SimpleCacheManager();
		List<Cache> caches = new ArrayList<>();
		for (String cacheName : cacheNames) {
			caches.add(new ConcurrentMapCache(cacheName));
		}
		result.setCaches(caches);
		result.afterPropertiesSet();
		return result;
	}


	/**
	 * Assert the following key is not held within the specified cache(s).
	 */
	public static void assertCacheMiss(Object key, Cache... caches) {
		for (Cache cache : caches) {
			assertNull("No entry in " + cache + " should have been found with key " + key, cache.get(key));
		}
	}

	/**
	 * Assert the following key has a matching value within the specified cache(s).
	 */
	public static void assertCacheHit(Object key, Object value, Cache... caches) {
		for (Cache cache : caches) {
			Cache.ValueWrapper wrapper = cache.get(key);
			assertNotNull("An entry in " + cache + " should have been found with key " + key, wrapper);
			assertEquals("Wrong value in " + cache + " for entry with key " + key, value, wrapper.get());
		}
	}

}
