

package org.springframework.cache.jcache.support;

import java.lang.annotation.Annotation;
import javax.cache.Cache;
import javax.cache.annotation.CacheInvocationContext;
import javax.cache.annotation.CacheResolver;

import static org.mockito.BDDMockito.*;


public class TestableCacheResolver implements CacheResolver {

	@Override
	public <K, V> Cache<K, V> resolveCache(CacheInvocationContext<? extends Annotation> cacheInvocationContext) {
		String cacheName = cacheInvocationContext.getCacheName();
		Cache<K, V> mock = mock(Cache.class);
		given(mock.getName()).willReturn(cacheName);
		return mock;
	}

}
