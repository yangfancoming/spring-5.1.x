

package org.springframework.cache.jcache.config;

import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.lang.Nullable;

/**
 * Extension of {@link CachingConfigurer} for the JSR-107 implementation.
 *
 * xmlBeanDefinitionReaderTo be implemented by classes annotated with
 * {@link org.springframework.cache.annotation.EnableCaching} that wish
 * or need to specify explicitly how exception caches are resolved for
 * annotation-driven cache management. Consider extending {@link JCacheConfigurerSupport},
 * which provides a stub implementation of all interface methods.
 *
 * xmlBeanDefinitionReaderSee {@link org.springframework.cache.annotation.EnableCaching} for
 * general examples and context; see {@link #exceptionCacheResolver()} for
 * detailed instructions.
 *
 * @author Stephane Nicoll
 * @since 4.1
 * @see CachingConfigurer
 * @see JCacheConfigurerSupport
 * @see org.springframework.cache.annotation.EnableCaching
 */
public interface JCacheConfigurer extends CachingConfigurer {

	/**
	 * Return the {@link CacheResolver} bean to use to resolve exception caches for
	 * annotation-driven cache management. Implementations must explicitly declare
	 * {@link org.springframework.context.annotation.Bean @Bean}, e.g.
	 * <pre class="code">
	 * &#064;Configuration
	 * &#064;EnableCaching
	 * public class AppConfig extends JCacheConfigurerSupport {
	 *     &#064;Bean // important!
	 *     &#064;Override
	 *     public CacheResolver exceptionCacheResolver() {
	 *         // configure and return CacheResolver instance
	 *     }
	 *     // ...
	 * }
	 * </pre>
	 * See {@link org.springframework.cache.annotation.EnableCaching} for more complete examples.
	 */
	@Nullable
	CacheResolver exceptionCacheResolver();

}
