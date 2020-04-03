

package org.springframework.cache.jcache.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import javax.cache.annotation.CacheInvocationContext;
import javax.cache.annotation.CacheMethodDetails;
import javax.cache.annotation.CacheResolver;
import javax.cache.annotation.CacheResult;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.cache.Cache;
import org.springframework.cache.jcache.AbstractJCacheTests;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;


public class CacheResolverAdapterTests extends AbstractJCacheTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();


	@Test
	public void resolveSimpleCache() throws Exception {
		DefaultCacheInvocationContext<?> dummyContext = createDummyContext();
		CacheResolverAdapter adapter = new CacheResolverAdapter(getCacheResolver(dummyContext, "testCache"));
		Collection<? extends Cache> caches = adapter.resolveCaches(dummyContext);
		assertNotNull(caches);
		assertEquals(1, caches.size());
		assertEquals("testCache", caches.iterator().next().getName());
	}

	@Test
	public void resolveUnknownCache() throws Exception {
		DefaultCacheInvocationContext<?> dummyContext = createDummyContext();
		CacheResolverAdapter adapter = new CacheResolverAdapter(getCacheResolver(dummyContext, null));

		thrown.expect(IllegalStateException.class);
		adapter.resolveCaches(dummyContext);
	}

	protected CacheResolver getCacheResolver(CacheInvocationContext<? extends Annotation> context, String cacheName) {
		CacheResolver cacheResolver = mock(CacheResolver.class);
		javax.cache.Cache cache;
		if (cacheName == null) {
			cache = null;
		}
		else {
			cache = mock(javax.cache.Cache.class);
			given(cache.getName()).willReturn(cacheName);
		}
		given(cacheResolver.resolveCache(context)).willReturn(cache);
		return cacheResolver;
	}

	protected DefaultCacheInvocationContext<?> createDummyContext() throws Exception {
		Method method = Sample.class.getMethod("get", String.class);
		CacheResult cacheAnnotation = method.getAnnotation(CacheResult.class);
		CacheMethodDetails<CacheResult> methodDetails =
				new DefaultCacheMethodDetails<>(method, cacheAnnotation, "test");
		CacheResultOperation operation = new CacheResultOperation(methodDetails,
				defaultCacheResolver, defaultKeyGenerator, defaultExceptionCacheResolver);
		return new DefaultCacheInvocationContext<>(operation, new Sample(), new Object[] {"id"});
	}


	static class Sample {

		@CacheResult
		public Object get(String id) {
			return null;
		}
	}

}
