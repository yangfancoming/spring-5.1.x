

package org.springframework.cache.jcache.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/**
 * A Pointcut that matches if the underlying {@link JCacheOperationSource}
 * has an operation for a given method.
 *
 * @author Stephane Nicoll
 * @since 4.1
 */
@SuppressWarnings("serial")
public abstract class JCacheOperationSourcePointcut extends StaticMethodMatcherPointcut implements Serializable {

	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		JCacheOperationSource cas = getCacheOperationSource();
		return (cas != null && cas.getCacheOperation(method, targetClass) != null);
	}

	/**
	 * Obtain the underlying {@link JCacheOperationSource} (may be {@code null}).
	 * To be implemented by subclasses.
	 */
	@Nullable
	protected abstract JCacheOperationSource getCacheOperationSource();


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof JCacheOperationSourcePointcut)) {
			return false;
		}
		JCacheOperationSourcePointcut otherPc = (JCacheOperationSourcePointcut) other;
		return ObjectUtils.nullSafeEquals(getCacheOperationSource(), otherPc.getCacheOperationSource());
	}

	@Override
	public int hashCode() {
		return JCacheOperationSourcePointcut.class.hashCode();
	}

	@Override
	public String toString() {
		return getClass().getName() + ": " + getCacheOperationSource();
	}

}
