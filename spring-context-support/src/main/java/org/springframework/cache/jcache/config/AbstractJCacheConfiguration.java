

package org.springframework.cache.jcache.config;

import java.util.function.Supplier;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cache.annotation.AbstractCachingConfiguration;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.jcache.interceptor.DefaultJCacheOperationSource;
import org.springframework.cache.jcache.interceptor.JCacheOperationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.lang.Nullable;

/**
 * Abstract JSR-107 specific {@code @Configuration} class providing common
 * structure for enabling JSR-107 annotation-driven cache management capability.
 *
 * @author Stephane Nicoll

 * @since 4.1
 * @see JCacheConfigurer
 */
@Configuration
public class AbstractJCacheConfiguration extends AbstractCachingConfiguration {

	@Nullable
	protected Supplier<CacheResolver> exceptionCacheResolver;


	@Override
	protected void useCachingConfigurer(CachingConfigurer config) {
		super.useCachingConfigurer(config);
		if (config instanceof JCacheConfigurer) {
			this.exceptionCacheResolver = ((JCacheConfigurer) config)::exceptionCacheResolver;
		}
	}

	@Bean(name = "jCacheOperationSource")
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public JCacheOperationSource cacheOperationSource() {
		return new DefaultJCacheOperationSource(
				this.cacheManager, this.cacheResolver, this.exceptionCacheResolver, this.keyGenerator);
	}

}
