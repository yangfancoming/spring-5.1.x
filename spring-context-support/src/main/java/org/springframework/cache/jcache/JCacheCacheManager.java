

package org.springframework.cache.jcache;

import java.util.Collection;
import java.util.LinkedHashSet;
import javax.cache.CacheManager;
import javax.cache.Caching;

import org.springframework.cache.Cache;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * {@link org.springframework.cache.CacheManager} implementation
 * backed by a JCache {@link CacheManager javax.cache.CacheManager}.
 *
 * xmlBeanDefinitionReaderNote: This class has been updated for JCache 1.0, as of Spring 4.0.
 *

 * @author Stephane Nicoll
 * @since 3.2
 */
public class JCacheCacheManager extends AbstractTransactionSupportingCacheManager {

	@Nullable
	private CacheManager cacheManager;

	private boolean allowNullValues = true;


	/**
	 * Create a new {@code JCacheCacheManager} without a backing JCache
	 * {@link CacheManager javax.cache.CacheManager}.
	 * xmlBeanDefinitionReaderThe backing JCache {@code javax.cache.CacheManager} can be set via the
	 * {@link #setCacheManager} bean property.
	 */
	public JCacheCacheManager() {
	}

	/**
	 * Create a new {@code JCacheCacheManager} for the given backing JCache
	 * {@link CacheManager javax.cache.CacheManager}.
	 * @param cacheManager the backing JCache {@code javax.cache.CacheManager}
	 */
	public JCacheCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}


	/**
	 * Set the backing JCache {@link CacheManager javax.cache.CacheManager}.
	 */
	public void setCacheManager(@Nullable CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	/**
	 * Return the backing JCache {@link CacheManager javax.cache.CacheManager}.
	 */
	@Nullable
	public CacheManager getCacheManager() {
		return this.cacheManager;
	}

	/**
	 * Specify whether to accept and convert {@code null} values for all caches
	 * in this cache manager.
	 * xmlBeanDefinitionReaderDefault is "true", despite JSR-107 itself not supporting {@code null} values.
	 * An internal holder object will be used to store user-level {@code null}s.
	 */
	public void setAllowNullValues(boolean allowNullValues) {
		this.allowNullValues = allowNullValues;
	}

	/**
	 * Return whether this cache manager accepts and converts {@code null} values
	 * for all of its caches.
	 */
	public boolean isAllowNullValues() {
		return this.allowNullValues;
	}

	@Override
	public void afterPropertiesSet() {
		if (getCacheManager() == null) {
			setCacheManager(Caching.getCachingProvider().getCacheManager());
		}
		super.afterPropertiesSet();
	}


	@Override
	protected Collection<Cache> loadCaches() {
		CacheManager cacheManager = getCacheManager();
		Assert.state(cacheManager != null, "No CacheManager set");

		Collection<Cache> caches = new LinkedHashSet<>();
		for (String cacheName : cacheManager.getCacheNames()) {
			javax.cache.Cache<Object, Object> jcache = cacheManager.getCache(cacheName);
			caches.add(new JCacheCache(jcache, isAllowNullValues()));
		}
		return caches;
	}

	@Override
	protected Cache getMissingCache(String name) {
		CacheManager cacheManager = getCacheManager();
		Assert.state(cacheManager != null, "No CacheManager set");

		// Check the JCache cache again (in case the cache was added at runtime)
		javax.cache.Cache<Object, Object> jcache = cacheManager.getCache(name);
		if (jcache != null) {
			return new JCacheCache(jcache, isAllowNullValues());
		}
		return null;
	}

}
