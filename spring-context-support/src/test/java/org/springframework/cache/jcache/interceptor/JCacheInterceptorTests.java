

package org.springframework.cache.jcache.interceptor;

import java.lang.reflect.Method;

import org.junit.Test;

import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.NamedCacheResolver;
import org.springframework.cache.jcache.AbstractJCacheTests;
import org.springframework.util.ReflectionUtils;

import static org.junit.Assert.*;


public class JCacheInterceptorTests extends AbstractJCacheTests {

	private final CacheOperationInvoker dummyInvoker = new DummyInvoker(null);

	@Test
	public void severalCachesNotSupported() {
		JCacheInterceptor interceptor = createInterceptor(createOperationSource(
				cacheManager, new NamedCacheResolver(cacheManager, "default", "simpleCache"),
				defaultExceptionCacheResolver, defaultKeyGenerator));

		AnnotatedJCacheableService service = new AnnotatedJCacheableService(cacheManager.getCache("default"));
		Method m = ReflectionUtils.findMethod(AnnotatedJCacheableService.class, "cache", String.class);

		try {
			interceptor.execute(dummyInvoker, service, m, new Object[] {"myId"});
		}
		catch (IllegalStateException ex) {
			assertTrue(ex.getMessage().contains("JSR-107 only supports a single cache"));
		}
		catch (Throwable ex) {
			fail("Unexpected: " + ex);
		}
	}

	@Test
	public void noCacheCouldBeResolved() {
		JCacheInterceptor interceptor = createInterceptor(createOperationSource(
				cacheManager, new NamedCacheResolver(cacheManager), // Returns empty list
				defaultExceptionCacheResolver, defaultKeyGenerator));

		AnnotatedJCacheableService service = new AnnotatedJCacheableService(cacheManager.getCache("default"));
		Method m = ReflectionUtils.findMethod(AnnotatedJCacheableService.class, "cache", String.class);

		try {
			interceptor.execute(dummyInvoker, service, m, new Object[] {"myId"});
		}
		catch (IllegalStateException ex) {
			assertTrue(ex.getMessage().contains("Cache could not have been resolved for"));
		}
		catch (Throwable ex) {
			fail("Unexpected: " + ex);
		}
	}

	@Test
	public void cacheManagerMandatoryIfCacheResolverNotSet() {
		thrown.expect(IllegalStateException.class);
		createOperationSource(null, null, null, defaultKeyGenerator);
	}

	@Test
	public void cacheManagerOptionalIfCacheResolversSet() {
		createOperationSource(null, defaultCacheResolver, defaultExceptionCacheResolver, defaultKeyGenerator);
	}

	@Test
	public void cacheResultReturnsProperType() throws Throwable {
		JCacheInterceptor interceptor = createInterceptor(createOperationSource(
				cacheManager, defaultCacheResolver, defaultExceptionCacheResolver, defaultKeyGenerator));

		AnnotatedJCacheableService service = new AnnotatedJCacheableService(cacheManager.getCache("default"));
		Method method = ReflectionUtils.findMethod(AnnotatedJCacheableService.class, "cache", String.class);

		CacheOperationInvoker invoker = new DummyInvoker(0L);
		Object execute = interceptor.execute(invoker, service, method, new Object[] {"myId"});
		assertNotNull("result cannot be null.", execute);
		assertEquals("Wrong result type", Long.class, execute.getClass());
		assertEquals("Wrong result", 0L, execute);
	}

	protected JCacheOperationSource createOperationSource(CacheManager cacheManager,
			CacheResolver cacheResolver, CacheResolver exceptionCacheResolver, KeyGenerator keyGenerator) {

		DefaultJCacheOperationSource source = new DefaultJCacheOperationSource();
		source.setCacheManager(cacheManager);
		source.setCacheResolver(cacheResolver);
		source.setExceptionCacheResolver(exceptionCacheResolver);
		source.setKeyGenerator(keyGenerator);
		source.setBeanFactory(new StaticListableBeanFactory());
		source.afterSingletonsInstantiated();
		return source;
	}


	protected JCacheInterceptor createInterceptor(JCacheOperationSource source) {
		JCacheInterceptor interceptor = new JCacheInterceptor();
		interceptor.setCacheOperationSource(source);
		interceptor.afterPropertiesSet();
		return interceptor;
	}


	private static class DummyInvoker implements CacheOperationInvoker {

		private final Object result;

		private DummyInvoker(Object result) {
			this.result = result;
		}

		@Override
		public Object invoke() throws ThrowableWrapper {
			return result;
		}
	}

}
