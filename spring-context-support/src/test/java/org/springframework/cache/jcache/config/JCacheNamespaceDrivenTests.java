

package org.springframework.cache.jcache.config;

import org.junit.Test;

import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.jcache.interceptor.DefaultJCacheOperationSource;
import org.springframework.cache.jcache.interceptor.JCacheInterceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import static org.junit.Assert.*;


public class JCacheNamespaceDrivenTests extends AbstractJCacheAnnotationTests {

	@Override
	protected ApplicationContext getApplicationContext() {
		return new GenericXmlApplicationContext(
				"/org/springframework/cache/jcache/config/jCacheNamespaceDriven.xml");
	}

	@Test
	public void cacheResolver() {
		ConfigurableApplicationContext context = new GenericXmlApplicationContext(
				"/org/springframework/cache/jcache/config/jCacheNamespaceDriven-resolver.xml");

		DefaultJCacheOperationSource ci = context.getBean(DefaultJCacheOperationSource.class);
		assertSame(context.getBean("cacheResolver"), ci.getCacheResolver());
		context.close();
	}

	@Test
	public void testCacheErrorHandler() {
		JCacheInterceptor ci = ctx.getBean(JCacheInterceptor.class);
		assertSame(ctx.getBean("errorHandler", CacheErrorHandler.class), ci.getErrorHandler());
	}

}
