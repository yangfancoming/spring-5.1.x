

package org.springframework.cache.config;

import org.junit.Assert;
import org.junit.Test;

import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;


public class CacheAdviceNamespaceTests extends AbstractCacheAnnotationTests {

	@Override
	protected ConfigurableApplicationContext getApplicationContext() {
		return new GenericXmlApplicationContext(
				"/org/springframework/cache/config/cache-advice.xml");
	}

	@Test
	public void testKeyStrategy() throws Exception {
		CacheInterceptor bean = this.ctx.getBean("cacheAdviceClass", CacheInterceptor.class);
		Assert.assertSame(this.ctx.getBean("keyGenerator"), bean.getKeyGenerator());
	}

}
