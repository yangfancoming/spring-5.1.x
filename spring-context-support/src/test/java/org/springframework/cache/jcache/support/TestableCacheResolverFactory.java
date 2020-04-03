

package org.springframework.cache.jcache.support;

import java.lang.annotation.Annotation;
import javax.cache.annotation.CacheMethodDetails;
import javax.cache.annotation.CacheResolver;
import javax.cache.annotation.CacheResolverFactory;
import javax.cache.annotation.CacheResult;


public class TestableCacheResolverFactory implements CacheResolverFactory {

	@Override
	public CacheResolver getCacheResolver(CacheMethodDetails<? extends Annotation> cacheMethodDetails) {
		return new TestableCacheResolver();
	}

	@Override
	public CacheResolver getExceptionCacheResolver(CacheMethodDetails<CacheResult> cacheMethodDetails) {
		return new TestableCacheResolver();
	}

}
