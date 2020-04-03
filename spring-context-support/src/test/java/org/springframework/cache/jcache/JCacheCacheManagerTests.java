

package org.springframework.cache.jcache;

import java.util.ArrayList;
import java.util.List;
import javax.cache.Cache;
import javax.cache.CacheManager;

import org.junit.Before;

import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManagerTests;

import static org.mockito.BDDMockito.*;


public class JCacheCacheManagerTests extends AbstractTransactionSupportingCacheManagerTests<JCacheCacheManager> {

	private CacheManagerMock cacheManagerMock;

	private JCacheCacheManager cacheManager;

	private JCacheCacheManager transactionalCacheManager;


	@Before
	public void setupOnce() {
		cacheManagerMock = new CacheManagerMock();
		cacheManagerMock.addCache(CACHE_NAME);

		cacheManager = new JCacheCacheManager(cacheManagerMock.getCacheManager());
		cacheManager.setTransactionAware(false);
		cacheManager.afterPropertiesSet();

		transactionalCacheManager = new JCacheCacheManager(cacheManagerMock.getCacheManager());
		transactionalCacheManager.setTransactionAware(true);
		transactionalCacheManager.afterPropertiesSet();
	}


	@Override
	protected JCacheCacheManager getCacheManager(boolean transactionAware) {
		if (transactionAware) {
			return transactionalCacheManager;
		}
		else {
			return cacheManager;
		}
	}

	@Override
	protected Class<? extends org.springframework.cache.Cache> getCacheType() {
		return JCacheCache.class;
	}

	@Override
	protected void addNativeCache(String cacheName) {
		cacheManagerMock.addCache(cacheName);
	}

	@Override
	protected void removeNativeCache(String cacheName) {
		cacheManagerMock.removeCache(cacheName);
	}


	private static class CacheManagerMock {

		private final List<String> cacheNames;

		private final CacheManager cacheManager;

		private CacheManagerMock() {
			this.cacheNames = new ArrayList<>();
			this.cacheManager = mock(CacheManager.class);
			given(cacheManager.getCacheNames()).willReturn(cacheNames);
		}

		private CacheManager getCacheManager() {
			return cacheManager;
		}

		@SuppressWarnings("unchecked")
		public void addCache(String name) {
			cacheNames.add(name);
			Cache cache = mock(Cache.class);
			given(cache.getName()).willReturn(name);
			given(cacheManager.getCache(name)).willReturn(cache);
		}

		public void removeCache(String name) {
			cacheNames.remove(name);
			given(cacheManager.getCache(name)).willReturn(null);
		}
	}

}
