

package org.springframework.cache.interceptor;

import java.lang.reflect.Method;
import java.util.Collection;

import org.springframework.lang.Nullable;

/**
 * Interface used by {@link CacheInterceptor}. Implementations know how to source
 * cache operation attributes, whether from configuration, metadata attributes at
 * source level, or elsewhere.
 *
 * @author Costin Leau

 * @since 3.1
 */
public interface CacheOperationSource {

	/**
	 * Return the collection of cache operations for this method,
	 * or {@code null} if the method contains no <em>cacheable</em> annotations.
	 * @param method the method to introspect
	 * @param targetClass the target class (may be {@code null}, in which case
	 * the declaring class of the method must be used)
	 * @return all cache operations for this method, or {@code null} if none found
	 */
	@Nullable
	Collection<CacheOperation> getCacheOperations(Method method, @Nullable Class<?> targetClass);

}
