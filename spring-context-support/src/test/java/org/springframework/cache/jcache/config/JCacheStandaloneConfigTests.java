

package org.springframework.cache.jcache.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;


public class JCacheStandaloneConfigTests extends AbstractJCacheAnnotationTests {

	@Override
	protected ApplicationContext getApplicationContext() {
		return new GenericXmlApplicationContext(
				"/org/springframework/cache/jcache/config/jCacheStandaloneConfig.xml");
	}

}
