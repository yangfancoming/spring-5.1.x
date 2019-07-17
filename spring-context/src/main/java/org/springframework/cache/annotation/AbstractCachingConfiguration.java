

package org.springframework.cache.annotation;

import java.util.Collection;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

/**
 * Abstract base {@code @Configuration} class providing common structure
 * for enabling Spring's annotation-driven cache management capability.

 * @author Stephane Nicoll

 * @since 3.1
 * @see EnableCaching
 */
@Configuration
public abstract class AbstractCachingConfiguration implements ImportAware {

	@Nullable
	protected AnnotationAttributes enableCaching;

	@Nullable
	protected Supplier<CacheManager> cacheManager;

	@Nullable
	protected Supplier<CacheResolver> cacheResolver;

	@Nullable
	protected Supplier<KeyGenerator> keyGenerator;

	@Nullable
	protected Supplier<CacheErrorHandler> errorHandler;


	@Override
	public void setImportMetadata(AnnotationMetadata importMetadata) {
		this.enableCaching = AnnotationAttributes.fromMap(
				importMetadata.getAnnotationAttributes(EnableCaching.class.getName(), false));
		if (this.enableCaching == null) {
			throw new IllegalArgumentException(
					"@EnableCaching is not present on importing class " + importMetadata.getClassName());
		}
	}

	@Autowired(required = false)
	void setConfigurers(Collection<CachingConfigurer> configurers) {
		if (CollectionUtils.isEmpty(configurers)) {
			return;
		}
		if (configurers.size() > 1) {
			throw new IllegalStateException(configurers.size() + " implementations of " +
					"CachingConfigurer were found when only 1 was expected. " +
					"Refactor the configuration such that CachingConfigurer is " +
					"implemented only once or not at all.");
		}
		CachingConfigurer configurer = configurers.iterator().next();
		useCachingConfigurer(configurer);
	}

	/**
	 * Extract the configuration from the nominated {@link CachingConfigurer}.
	 */
	protected void useCachingConfigurer(CachingConfigurer config) {
		this.cacheManager = config::cacheManager;
		this.cacheResolver = config::cacheResolver;
		this.keyGenerator = config::keyGenerator;
		this.errorHandler = config::errorHandler;
	}

}
