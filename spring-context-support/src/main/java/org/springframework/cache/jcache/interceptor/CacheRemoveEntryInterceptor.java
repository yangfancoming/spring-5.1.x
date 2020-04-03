

package org.springframework.cache.jcache.interceptor;

import javax.cache.annotation.CacheRemove;

import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheOperationInvoker;

/**
 * Intercept methods annotated with {@link CacheRemove}.
 *
 * @author Stephane Nicoll
 * @since 4.1
 */
@SuppressWarnings("serial")
class CacheRemoveEntryInterceptor extends AbstractKeyCacheInterceptor<CacheRemoveOperation, CacheRemove> {

	protected CacheRemoveEntryInterceptor(CacheErrorHandler errorHandler) {
		super(errorHandler);
	}


	@Override
	protected Object invoke(
			CacheOperationInvocationContext<CacheRemoveOperation> context, CacheOperationInvoker invoker) {

		CacheRemoveOperation operation = context.getOperation();

		boolean earlyRemove = operation.isEarlyRemove();
		if (earlyRemove) {
			removeValue(context);
		}

		try {
			Object result = invoker.invoke();
			if (!earlyRemove) {
				removeValue(context);
			}
			return result;
		}
		catch (CacheOperationInvoker.ThrowableWrapper wrapperException) {
			Throwable ex = wrapperException.getOriginal();
			if (!earlyRemove && operation.getExceptionTypeFilter().match(ex.getClass())) {
				removeValue(context);
			}
			throw wrapperException;
		}
	}

	private void removeValue(CacheOperationInvocationContext<CacheRemoveOperation> context) {
		Object key = generateKey(context);
		Cache cache = resolveCache(context);
		if (logger.isTraceEnabled()) {
			logger.trace("Invalidating key [" + key + "] on cache '" + cache.getName()
					+ "' for operation " + context.getOperation());
		}
		doEvict(cache, key);
	}

}
