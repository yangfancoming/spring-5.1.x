

package org.springframework.cache.interceptor;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.junit.Assert.*;

/**
 * Integration tests for {@link CacheProxyFactoryBean}.
 *
 * @author John Blum

 */
public class CacheProxyFactoryBeanTests {

	@Test
	public void configurationClassWithCacheProxyFactoryBean() {
		try (AnnotationConfigApplicationContext applicationContext =
				new AnnotationConfigApplicationContext(CacheProxyFactoryBeanConfiguration.class)) {
			Greeter greeter = applicationContext.getBean("greeter", Greeter.class);
			assertNotNull(greeter);
			assertFalse(greeter.isCacheMiss());
			assertEquals("Hello John!", greeter.greet("John"));
			assertTrue(greeter.isCacheMiss());
			assertEquals("Hello Jon!", greeter.greet("Jon"));
			assertTrue(greeter.isCacheMiss());
			assertEquals("Hello John!", greeter.greet("John"));
			assertFalse(greeter.isCacheMiss());
			assertEquals("Hello World!", greeter.greet());
			assertTrue(greeter.isCacheMiss());
			assertEquals("Hello World!", greeter.greet());
			assertFalse(greeter.isCacheMiss());
		}
	}


	@Configuration
	@EnableCaching
	static class CacheProxyFactoryBeanConfiguration {

		@Bean
		ConcurrentMapCacheManager cacheManager() {
			return new ConcurrentMapCacheManager("Greetings");
		}

		@Bean
		CacheProxyFactoryBean greeter() {
			CacheProxyFactoryBean factoryBean = new CacheProxyFactoryBean();
			factoryBean.setCacheOperationSources(newCacheOperationSource("greet", newCacheOperation("Greetings")));
			factoryBean.setTarget(new SimpleGreeter());
			return factoryBean;
		}

		CacheOperationSource newCacheOperationSource(String methodName, CacheOperation... cacheOperations) {
			NameMatchCacheOperationSource cacheOperationSource = new NameMatchCacheOperationSource();
			cacheOperationSource.addCacheMethod(methodName, Arrays.asList(cacheOperations));
			return cacheOperationSource;
		}

		CacheableOperation newCacheOperation(String cacheName) {
			CacheableOperation.Builder builder = new CacheableOperation.Builder();
			builder.setCacheManager("cacheManager");
			builder.setCacheName(cacheName);
			return builder.build();
		}
	}


	interface Greeter {

		default boolean isCacheHit() {
			return !isCacheMiss();
		}

		boolean isCacheMiss();

		void setCacheMiss();

		default String greet() {
			return greet("World");
		}

		default String greet(String name) {
			setCacheMiss();
			return String.format("Hello %s!", name);
		}
	}


	static class SimpleGreeter implements Greeter {

		private final AtomicBoolean cacheMiss = new AtomicBoolean(false);

		@Override
		public boolean isCacheMiss() {
			return this.cacheMiss.getAndSet(false);
		}

		@Override
		public void setCacheMiss() {
			this.cacheMiss.set(true);
		}
	}

}
