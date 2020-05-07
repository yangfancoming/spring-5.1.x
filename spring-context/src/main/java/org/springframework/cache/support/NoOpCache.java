

package org.springframework.cache.support;

import java.util.concurrent.Callable;

import org.springframework.cache.Cache;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * A no operation {@link Cache} implementation suitable for disabling caching.
 *
 * Will simply accept any items into the cache not actually storing them.
 * @since 4.3.4
 */
public class NoOpCache implements Cache {

	private final String name;

	/**
	 * Create a {@link NoOpCache} instance with the specified name.
	 * @param name the name of the cache
	 */
	public NoOpCache(String name) {
		Assert.notNull(name, "Cache name must not be null");
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Object getNativeCache() {
		return this;
	}

	@Override
	@Nullable
	public ValueWrapper get(Object key) {
		return null;
	}

	@Override
	@Nullable
	public <T> T get(Object key, @Nullable Class<T> type) {
		return null;
	}

	@Override
	@Nullable
	public <T> T get(Object key, Callable<T> valueLoader) {
		try {
			return valueLoader.call();
		}catch (Exception ex) {
			throw new ValueRetrievalException(key, valueLoader, ex);
		}
	}

	@Override
	public void put(Object key, @Nullable Object value) {
	}

	@Override
	@Nullable
	public ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
		return null;
	}

	@Override
	public void evict(Object key) {
	}

	@Override
	public void clear() {
	}

}
