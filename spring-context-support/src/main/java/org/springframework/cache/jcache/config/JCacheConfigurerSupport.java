

package org.springframework.cache.jcache.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.lang.Nullable;

/**
 * An extension of {@link CachingConfigurerSupport} that also implements
 * {@link JCacheConfigurer}.
 *
 * xmlBeanDefinitionReaderUsers of JSR-107 annotations may extend from this class rather than
 * implementing from {@link JCacheConfigurer} directly.
 * @since 4.1
 * @see JCacheConfigurer
 * @see CachingConfigurerSupport
 */
public class JCacheConfigurerSupport extends CachingConfigurerSupport implements JCacheConfigurer {

	@Override
	@Nullable
	public CacheResolver exceptionCacheResolver() {
		return null;
	}

}
