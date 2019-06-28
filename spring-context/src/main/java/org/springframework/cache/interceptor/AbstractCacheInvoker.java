

package org.springframework.cache.interceptor;

import org.springframework.cache.Cache;
import org.springframework.lang.Nullable;
import org.springframework.util.function.SingletonSupplier;

/**
 * A base component for invoking {@link Cache} operations and using a
 * configurable {@link CacheErrorHandler} when an exception occurs.
 *
 * @author Stephane Nicoll
 * @author Juergen Hoeller
 * @since 4.1
 * @see org.springframework.cache.interceptor.CacheErrorHandler
 */
public abstract class AbstractCacheInvoker {

	protected SingletonSupplier<CacheErrorHandler> errorHandler;


	protected AbstractCacheInvoker() {
		this.errorHandler = SingletonSupplier.of(SimpleCacheErrorHandler::new);
	}

	protected AbstractCacheInvoker(CacheErrorHandler errorHandler) {
		this.errorHandler = SingletonSupplier.of(errorHandler);
	}


	/**
	 * Set the {@link CacheErrorHandler} instance to use to handle errors
	 * thrown by the cache provider. By default, a {@link SimpleCacheErrorHandler}
	 * is used who throws any exception as is.
	 */
	public void setErrorHandler(CacheErrorHandler errorHandler) {
		this.errorHandler = SingletonSupplier.of(errorHandler);
	}

	/**
	 * Return the {@link CacheErrorHandler} to use.
	 */
	public CacheErrorHandler getErrorHandler() {
		return this.errorHandler.obtain();
	}


	/**
	 * Execute {@link Cache#get(Object)} on the specified {@link Cache} and
	 * invoke the error handler if an exception occurs. Return {@code null}
	 * if the handler does not throw any exception, which simulates a cache
	 * miss in case of error.
	 * @see Cache#get(Object)
	 */
	@Nullable
	protected Cache.ValueWrapper doGet(Cache cache, Object key) {
		try {
			return cache.get(key);
		}
		catch (RuntimeException ex) {
			getErrorHandler().handleCacheGetError(ex, cache, key);
			return null;  // If the exception is handled, return a cache miss
		}
	}

	/**
	 * Execute {@link Cache#put(Object, Object)} on the specified {@link Cache}
	 * and invoke the error handler if an exception occurs.
	 */
	protected void doPut(Cache cache, Object key, @Nullable Object result) {
		try {
			cache.put(key, result);
		}
		catch (RuntimeException ex) {
			getErrorHandler().handleCachePutError(ex, cache, key, result);
		}
	}

	/**
	 * Execute {@link Cache#evict(Object)} on the specified {@link Cache} and
	 * invoke the error handler if an exception occurs.
	 */
	protected void doEvict(Cache cache, Object key) {
		try {
			cache.evict(key);
		}
		catch (RuntimeException ex) {
			getErrorHandler().handleCacheEvictError(ex, cache, key);
		}
	}

	/**
	 * Execute {@link Cache#clear()} on the specified {@link Cache} and
	 * invoke the error handler if an exception occurs.
	 */
	protected void doClear(Cache cache) {
		try {
			cache.clear();
		}
		catch (RuntimeException ex) {
			getErrorHandler().handleCacheClearError(ex, cache);
		}
	}

}
