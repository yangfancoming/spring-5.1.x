

package org.springframework.cache.support;

import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.lang.Nullable;

/**
 * Straightforward implementation of {@link org.springframework.cache.Cache.ValueWrapper},
 * simply holding the value as given at construction and returning it from {@link #get()}.
 *
 * @author Costin Leau
 * @since 3.1
 */
public class SimpleValueWrapper implements ValueWrapper {

	@Nullable
	private final Object value;


	/**
	 * Create a new SimpleValueWrapper instance for exposing the given value.
	 * @param value the value to expose (may be {@code null})
	 */
	public SimpleValueWrapper(@Nullable Object value) {
		this.value = value;
	}


	/**
	 * Simply returns the value as given at construction time.
	 */
	@Override
	@Nullable
	public Object get() {
		return this.value;
	}

}
