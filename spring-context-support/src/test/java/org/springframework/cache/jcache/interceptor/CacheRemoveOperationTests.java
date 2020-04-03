

package org.springframework.cache.jcache.interceptor;

import javax.cache.annotation.CacheInvocationParameter;
import javax.cache.annotation.CacheMethodDetails;
import javax.cache.annotation.CacheRemove;

import org.junit.Test;

import static org.junit.Assert.*;


public class CacheRemoveOperationTests extends AbstractCacheOperationTests<CacheRemoveOperation> {

	@Override
	protected CacheRemoveOperation createSimpleOperation() {
		CacheMethodDetails<CacheRemove> methodDetails = create(CacheRemove.class,
				SampleObject.class, "simpleRemove", Long.class);

		return new CacheRemoveOperation(methodDetails, defaultCacheResolver, defaultKeyGenerator);
	}

	@Test
	public void simpleRemove() {
		CacheRemoveOperation operation = createSimpleOperation();

		CacheInvocationParameter[] allParameters = operation.getAllParameters(2L);
		assertEquals(1, allParameters.length);
		assertCacheInvocationParameter(allParameters[0], Long.class, 2L, 0);
	}

}
