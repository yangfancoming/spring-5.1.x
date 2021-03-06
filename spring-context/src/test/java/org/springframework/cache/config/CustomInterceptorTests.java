

package org.springframework.cache.config;

import java.io.IOException;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.cache.CacheManager;
import org.springframework.cache.CacheTestUtils;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.junit.Assert.*;


public class CustomInterceptorTests {

	protected ConfigurableApplicationContext ctx;

	protected CacheableService<?> cs;

	@Before
	public void setup() {
		this.ctx = new AnnotationConfigApplicationContext(EnableCachingConfig.class);
		this.cs = ctx.getBean("service", CacheableService.class);
	}

	@After
	public void tearDown() {
		this.ctx.close();
	}

	@Test
	public void onlyOneInterceptorIsAvailable() {
		Map<String, CacheInterceptor> interceptors = this.ctx.getBeansOfType(CacheInterceptor.class);
		assertEquals("Only one interceptor should be defined", 1, interceptors.size());
		CacheInterceptor interceptor = interceptors.values().iterator().next();
		assertEquals("Custom interceptor not defined", TestCacheInterceptor.class, interceptor.getClass());
	}

	@Test
	public void customInterceptorAppliesWithRuntimeException() {
		Object o = this.cs.throwUnchecked(0L);
		assertEquals(55L, o); // See TestCacheInterceptor
	}

	@Test
	public void customInterceptorAppliesWithCheckedException() {
		try {
			this.cs.throwChecked(0L);
			fail("Should have failed");
		}
		catch (RuntimeException e) {
			assertNotNull("missing original exception", e.getCause());
			assertEquals(IOException.class, e.getCause().getClass());
		}
		catch (Exception e) {
			fail("Wrong exception type " + e);
		}
	}


	@Configuration
	@EnableCaching
	static class EnableCachingConfig {

		@Bean
		public CacheManager cacheManager() {
			return CacheTestUtils.createSimpleCacheManager("testCache", "primary", "secondary");
		}

		@Bean
		public CacheableService<?> service() {
			return new DefaultCacheableService();
		}

		@Bean
		public CacheInterceptor cacheInterceptor(CacheOperationSource cacheOperationSource) {
			CacheInterceptor cacheInterceptor = new TestCacheInterceptor();
			cacheInterceptor.setCacheManager(cacheManager());
			cacheInterceptor.setCacheOperationSources(cacheOperationSource);
			return cacheInterceptor;
		}
	}

	/**
	 * A test {@link CacheInterceptor} that handles special exception
	 * types.
	 */
	@SuppressWarnings("serial")
	static class TestCacheInterceptor extends CacheInterceptor {

		@Override
		protected Object invokeOperation(CacheOperationInvoker invoker) {
			try {
				return super.invokeOperation(invoker);
			}
			catch (CacheOperationInvoker.ThrowableWrapper e) {
				Throwable original = e.getOriginal();
				if (original.getClass() == UnsupportedOperationException.class) {
					return 55L;
				}
				else {
					throw new CacheOperationInvoker.ThrowableWrapper(
							new RuntimeException("wrapping original", original));
				}
			}
		}
	}

}
