

package org.springframework.cache;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.cache.support.AbstractValueAdaptingCache;

/**
 * @author Stephane Nicoll
 */
public abstract class AbstractValueAdaptingCacheTests<T extends AbstractValueAdaptingCache>
		extends AbstractCacheTests<T>  {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	protected final static String CACHE_NAME_NO_NULL = "testCacheNoNull";

	protected abstract T getCache(boolean allowNull);

	@Test
	public void testCachePutNullValueAllowNullFalse() {
		T cache = getCache(false);
		String key = createRandomKey();

		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage(CACHE_NAME_NO_NULL);
		this.thrown.expectMessage(
				"is configured to not allow null values but null was provided");
		cache.put(key, null);
	}

}
