

package org.springframework.cache.ehcache;

import java.util.Collection;
import java.util.LinkedHashSet;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Status;

import org.springframework.cache.Cache;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * CacheManager backed by an EhCache {@link net.sf.ehcache.CacheManager}.
 *
 * @author Costin Leau

 * @author Stephane Nicoll
 * @since 3.1
 */
public class EhCacheCacheManager extends AbstractTransactionSupportingCacheManager {

	@Nullable
	private net.sf.ehcache.CacheManager cacheManager;


	/**
	 * Create a new EhCacheCacheManager, setting the target EhCache CacheManager
	 * through the {@link #setCacheManager} bean property.
	 */
	public EhCacheCacheManager() {
	}

	/**
	 * Create a new EhCacheCacheManager for the given backing EhCache CacheManager.
	 * @param cacheManager the backing EhCache {@link net.sf.ehcache.CacheManager}
	 */
	public EhCacheCacheManager(net.sf.ehcache.CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}


	/**
	 * Set the backing EhCache {@link net.sf.ehcache.CacheManager}.
	 */
	public void setCacheManager(@Nullable net.sf.ehcache.CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	/**
	 * Return the backing EhCache {@link net.sf.ehcache.CacheManager}.
	 */
	@Nullable
	public net.sf.ehcache.CacheManager getCacheManager() {
		return this.cacheManager;
	}

	@Override
	public void afterPropertiesSet() {
		if (getCacheManager() == null) {
			setCacheManager(EhCacheManagerUtils.buildCacheManager());
		}
		super.afterPropertiesSet();
	}


	@Override
	protected Collection<Cache> loadCaches() {
		net.sf.ehcache.CacheManager cacheManager = getCacheManager();
		Assert.state(cacheManager != null, "No CacheManager set");

		Status status = cacheManager.getStatus();
		if (!Status.STATUS_ALIVE.equals(status)) {
			throw new IllegalStateException(
					"An 'alive' EhCache CacheManager is required - current cache is " + status.toString());
		}

		String[] names = getCacheManager().getCacheNames();
		Collection<Cache> caches = new LinkedHashSet<>(names.length);
		for (String name : names) {
			caches.add(new EhCacheCache(getCacheManager().getEhcache(name)));
		}
		return caches;
	}

	@Override
	protected Cache getMissingCache(String name) {
		net.sf.ehcache.CacheManager cacheManager = getCacheManager();
		Assert.state(cacheManager != null, "No CacheManager set");

		// Check the EhCache cache again (in case the cache was added at runtime)
		Ehcache ehcache = cacheManager.getEhcache(name);
		if (ehcache != null) {
			return new EhCacheCache(ehcache);
		}
		return null;
	}

}
