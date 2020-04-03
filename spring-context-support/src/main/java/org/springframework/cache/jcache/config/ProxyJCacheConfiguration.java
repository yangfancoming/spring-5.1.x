

package org.springframework.cache.jcache.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cache.config.CacheManagementConfigUtils;
import org.springframework.cache.jcache.interceptor.BeanFactoryJCacheOperationSourceAdvisor;
import org.springframework.cache.jcache.interceptor.JCacheInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * {@code @Configuration} class that registers the Spring infrastructure beans necessary
 * to enable proxy-based annotation-driven JSR-107 cache management.
 *
 * xmlBeanDefinitionReaderCan safely be used alongside Spring's caching support.
 *
 * @author Stephane Nicoll

 * @since 4.1
 * @see org.springframework.cache.annotation.EnableCaching
 * @see org.springframework.cache.annotation.CachingConfigurationSelector
 */
@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ProxyJCacheConfiguration extends AbstractJCacheConfiguration {

	@Bean(name = CacheManagementConfigUtils.JCACHE_ADVISOR_BEAN_NAME)
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public BeanFactoryJCacheOperationSourceAdvisor cacheAdvisor() {
		BeanFactoryJCacheOperationSourceAdvisor advisor =
				new BeanFactoryJCacheOperationSourceAdvisor();
		advisor.setCacheOperationSource(cacheOperationSource());
		advisor.setAdvice(cacheInterceptor());
		if (this.enableCaching != null) {
			advisor.setOrder(this.enableCaching.<Integer>getNumber("order"));
		}
		return advisor;
	}

	@Bean(name = "jCacheInterceptor")
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public JCacheInterceptor cacheInterceptor() {
		JCacheInterceptor interceptor = new JCacheInterceptor(this.errorHandler);
		interceptor.setCacheOperationSource(cacheOperationSource());
		return interceptor;
	}

}
