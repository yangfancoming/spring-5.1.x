

package org.springframework.cache.jcache.interceptor;

import java.io.IOException;
import javax.cache.annotation.CacheInvocationParameter;
import javax.cache.annotation.CacheMethodDetails;
import javax.cache.annotation.CachePut;

import org.junit.Test;

import static org.junit.Assert.*;


public class CachePutOperationTests extends AbstractCacheOperationTests<CachePutOperation> {

	@Override
	protected CachePutOperation createSimpleOperation() {
		CacheMethodDetails<CachePut> methodDetails = create(CachePut.class,
				SampleObject.class, "simplePut", Long.class, SampleObject.class);
		return createDefaultOperation(methodDetails);
	}

	@Test
	public void simplePut() {
		CachePutOperation operation = createSimpleOperation();

		CacheInvocationParameter[] allParameters = operation.getAllParameters(2L, sampleInstance);
		assertEquals(2, allParameters.length);
		assertCacheInvocationParameter(allParameters[0], Long.class, 2L, 0);
		assertCacheInvocationParameter(allParameters[1], SampleObject.class, sampleInstance, 1);

		CacheInvocationParameter valueParameter = operation.getValueParameter(2L, sampleInstance);
		assertNotNull(valueParameter);
		assertCacheInvocationParameter(valueParameter, SampleObject.class, sampleInstance, 1);
	}

	@Test
	public void noCacheValue() {
		CacheMethodDetails<CachePut> methodDetails = create(CachePut.class,
				SampleObject.class, "noCacheValue", Long.class);

		thrown.expect(IllegalArgumentException.class);
		createDefaultOperation(methodDetails);
	}

	@Test
	public void multiCacheValues() {
		CacheMethodDetails<CachePut> methodDetails = create(CachePut.class,
				SampleObject.class, "multiCacheValues", Long.class, SampleObject.class, SampleObject.class);

		thrown.expect(IllegalArgumentException.class);
		createDefaultOperation(methodDetails);
	}

	@Test
	public void invokeWithWrongParameters() {
		CachePutOperation operation = createSimpleOperation();

		thrown.expect(IllegalStateException.class);
		operation.getValueParameter(2L);
	}

	@Test
	public void fullPutConfig() {
		CacheMethodDetails<CachePut> methodDetails = create(CachePut.class,
				SampleObject.class, "fullPutConfig", Long.class, SampleObject.class);
		CachePutOperation operation = createDefaultOperation(methodDetails);
		assertTrue(operation.isEarlyPut());
		assertNotNull(operation.getExceptionTypeFilter());
		assertTrue(operation.getExceptionTypeFilter().match(IOException.class));
		assertFalse(operation.getExceptionTypeFilter().match(NullPointerException.class));
	}

	private CachePutOperation createDefaultOperation(CacheMethodDetails<CachePut> methodDetails) {
		return new CachePutOperation(methodDetails, defaultCacheResolver, defaultKeyGenerator);
	}

}
