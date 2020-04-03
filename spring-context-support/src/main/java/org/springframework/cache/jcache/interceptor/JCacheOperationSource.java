

package org.springframework.cache.jcache.interceptor;

import java.lang.reflect.Method;

import org.springframework.lang.Nullable;

/**
 * Interface used by {@link JCacheInterceptor}. Implementations know how to source
 * cache operation attributes from standard JSR-107 annotations.
 *
 * @author Stephane Nicoll
 * @since 4.1
 * @see org.springframework.cache.interceptor.CacheOperationSource
 */
public interface JCacheOperationSource {

	/**
	 * Return the cache operations for this method, or {@code null}
	 * if the method contains no <em>JSR-107</em> related metadata.
	 * @param method the method to introspect
	 * @param targetClass the target class (may be {@code null}, in which case
	 * the declaring class of the method must be used)
	 * @return the cache operation for this method, or {@code null} if none found
	 */
	@Nullable
	JCacheOperation<?> getCacheOperation(Method method, @Nullable Class<?> targetClass);

}
