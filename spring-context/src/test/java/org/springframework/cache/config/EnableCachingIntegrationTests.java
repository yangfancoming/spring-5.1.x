

package org.springframework.cache.config;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.After;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.CacheTestUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.Assert.*;
import static org.springframework.cache.CacheTestUtils.*;

/**
 * Tests that represent real use cases with advanced configuration.
 *
 * @author Stephane Nicoll
 */
public class EnableCachingIntegrationTests {

	private ConfigurableApplicationContext context;


	@After
	public void closeContext() {
		if (this.context != null) {
			this.context.close();
		}
	}


	@Test
	public void fooServiceWithInterface() {
		this.context = new AnnotationConfigApplicationContext(FooConfig.class);
		FooService service = this.context.getBean(FooService.class);
		fooGetSimple(service);
	}

	@Test
	public void fooServiceWithInterfaceCglib() {
		this.context = new AnnotationConfigApplicationContext(FooConfigCglib.class);
		FooService service = this.context.getBean(FooService.class);
		fooGetSimple(service);
	}

	private void fooGetSimple(FooService service) {
		Cache cache = getCache();

		Object key = new Object();
		assertCacheMiss(key, cache);

		Object value = service.getSimple(key);
		assertCacheHit(key, value, cache);
	}

	@Test
	public void beanConditionOff() {
		this.context = new AnnotationConfigApplicationContext(BeanConditionConfig.class);
		FooService service = this.context.getBean(FooService.class);
		Cache cache = getCache();

		Object key = new Object();
		service.getWithCondition(key);
		assertCacheMiss(key, cache);
		service.getWithCondition(key);
		assertCacheMiss(key, cache);

		assertEquals(2, this.context.getBean(BeanConditionConfig.Bar.class).count);
	}

	@Test
	public void beanConditionOn() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.setEnvironment(new MockEnvironment().withProperty("bar.enabled", "true"));
		ctx.register(BeanConditionConfig.class);
		ctx.refresh();
		this.context = ctx;

		FooService service = this.context.getBean(FooService.class);
		Cache cache = getCache();

		Object key = new Object();
		Object value = service.getWithCondition(key);
		assertCacheHit(key, value, cache);
		value = service.getWithCondition(key);
		assertCacheHit(key, value, cache);

		assertEquals(2, this.context.getBean(BeanConditionConfig.Bar.class).count);
	}

	private Cache getCache() {
		return this.context.getBean(CacheManager.class).getCache("testCache");
	}


	@Configuration
	static class SharedConfig extends CachingConfigurerSupport {

		@Override
		@Bean
		public CacheManager cacheManager() {
			return CacheTestUtils.createSimpleCacheManager("testCache");
		}
	}


	@Configuration
	@Import(SharedConfig.class)
	@EnableCaching
	static class FooConfig {

		@Bean
		public FooService fooService() {
			return new FooServiceImpl();
		}
	}


	@Configuration
	@Import(SharedConfig.class)
	@EnableCaching(proxyTargetClass = true)
	static class FooConfigCglib {

		@Bean
		public FooService fooService() {
			return new FooServiceImpl();
		}
	}


	interface FooService {

		Object getSimple(Object key);

		Object getWithCondition(Object key);
	}


	@CacheConfig(cacheNames = "testCache")
	static class FooServiceImpl implements FooService {

		private final AtomicLong counter = new AtomicLong();

		@Override
		@Cacheable
		public Object getSimple(Object key) {
			return this.counter.getAndIncrement();
		}

		@Override
		@Cacheable(condition = "@bar.enabled")
		public Object getWithCondition(Object key) {
			return this.counter.getAndIncrement();
		}
	}


	@Configuration
	@Import(FooConfig.class)
	@EnableCaching
	static class BeanConditionConfig {

		@Autowired
		Environment env;

		@Bean
		public Bar bar() {
			return new Bar(Boolean.valueOf(env.getProperty("bar.enabled")));
		}


		static class Bar {

			public int count;

			private final boolean enabled;

			public Bar(boolean enabled) {
				this.enabled = enabled;
			}

			public boolean isEnabled() {
				this.count++;
				return this.enabled;
			}
		}
	}

}
