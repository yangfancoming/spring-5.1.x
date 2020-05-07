

package org.springframework.cache.annotation;

import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.lang.Nullable;

/**
 * An implementation of {@link CachingConfigurer} with empty methods allowing sub-classes to override only the methods they're interested in.
 * @since 4.1
 * @see CachingConfigurer
 */
public class CachingConfigurerSupport implements CachingConfigurer {

	@Override
	@Nullable
	public CacheManager cacheManager() {
		return null;
	}

	@Override
	@Nullable
	public CacheResolver cacheResolver() {
		return null;
	}

	@Override
	@Nullable
	public KeyGenerator keyGenerator() {
		return null;
	}

	@Override
	@Nullable
	public CacheErrorHandler errorHandler() {
		return null;
	}
}
