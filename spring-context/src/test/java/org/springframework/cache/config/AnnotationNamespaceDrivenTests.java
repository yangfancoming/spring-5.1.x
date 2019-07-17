

package org.springframework.cache.config;

import org.junit.Test;

import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import static org.junit.Assert.*;


public class AnnotationNamespaceDrivenTests extends AbstractCacheAnnotationTests {

	@Override
	protected ConfigurableApplicationContext getApplicationContext() {
		return new GenericXmlApplicationContext(
				"/org/springframework/cache/config/annotationDrivenCacheNamespace.xml");
	}

	@Test
	public void testKeyStrategy() {
		CacheInterceptor ci = this.ctx.getBean(
				"org.springframework.cache.interceptor.CacheInterceptor#0", CacheInterceptor.class);
		assertSame(this.ctx.getBean("keyGenerator"), ci.getKeyGenerator());
	}

	@Test
	public void cacheResolver() {
		ConfigurableApplicationContext context = new GenericXmlApplicationContext(
				"/org/springframework/cache/config/annotationDrivenCacheNamespace-resolver.xml");

		CacheInterceptor ci = context.getBean(CacheInterceptor.class);
		assertSame(context.getBean("cacheResolver"), ci.getCacheResolver());
		context.close();
	}

	@Test
	public void bothSetOnlyResolverIsUsed() {
		ConfigurableApplicationContext context = new GenericXmlApplicationContext(
				"/org/springframework/cache/config/annotationDrivenCacheNamespace-manager-resolver.xml");

		CacheInterceptor ci = context.getBean(CacheInterceptor.class);
		assertSame(context.getBean("cacheResolver"), ci.getCacheResolver());
		context.close();
	}

	@Test
	public void testCacheErrorHandler() {
		CacheInterceptor ci = this.ctx.getBean(
				"org.springframework.cache.interceptor.CacheInterceptor#0", CacheInterceptor.class);
		assertSame(this.ctx.getBean("errorHandler", CacheErrorHandler.class), ci.getErrorHandler());
	}

}
