

package org.springframework.cache.ehcache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import org.junit.After;
import org.junit.Before;

import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManagerTests;


public class EhCacheCacheManagerTests extends AbstractTransactionSupportingCacheManagerTests<EhCacheCacheManager> {

	private CacheManager nativeCacheManager;

	private EhCacheCacheManager cacheManager;

	private EhCacheCacheManager transactionalCacheManager;


	@Before
	public void setup() {
		nativeCacheManager = new CacheManager(new Configuration().name("EhCacheCacheManagerTests")
				.defaultCache(new CacheConfiguration("default", 100)));
		addNativeCache(CACHE_NAME);

		cacheManager = new EhCacheCacheManager(nativeCacheManager);
		cacheManager.setTransactionAware(false);
		cacheManager.afterPropertiesSet();

		transactionalCacheManager = new EhCacheCacheManager(nativeCacheManager);
		transactionalCacheManager.setTransactionAware(true);
		transactionalCacheManager.afterPropertiesSet();
	}

	@After
	public void shutdown() {
		nativeCacheManager.shutdown();
	}


	@Override
	protected EhCacheCacheManager getCacheManager(boolean transactionAware) {
		if (transactionAware) {
			return transactionalCacheManager;
		}
		else {
			return cacheManager;
		}
	}

	@Override
	protected Class<? extends org.springframework.cache.Cache> getCacheType() {
		return EhCacheCache.class;
	}

	@Override
	protected void addNativeCache(String cacheName) {
		nativeCacheManager.addCache(cacheName);
	}

	@Override
	protected void removeNativeCache(String cacheName) {
		nativeCacheManager.removeCache(cacheName);
	}

}
