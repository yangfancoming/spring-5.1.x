

package org.springframework.cache.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@code @CacheConfig} provides a mechanism for sharing common cache-related
 * settings at the class level.
 *
 * When this annotation is present on a given class, it provides a set
 * of default settings for any cache operation defined in that class.
 * @since 4.1
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheConfig {

	/**
	 * Names of the default caches to consider for caching operations defined
	 * in the annotated class.
	 * If none is set at the operation level, these are used instead of the default.
	 * May be used to determine the target cache (or caches), matching the
	 * qualifier value or the bean names of a specific bean definition.
	 */
	String[] cacheNames() default {};

	/**
	 * The bean name of the default {@link org.springframework.cache.interceptor.KeyGenerator} to
	 * use for the class.
	 * If none is set at the operation level, this one is used instead of the default.
	 * The key generator is mutually exclusive with the use of a custom key. When such key is
	 * defined for the operation, the value of this key generator is ignored.
	 */
	String keyGenerator() default "";

	/**
	 * The bean name of the custom {@link org.springframework.cache.CacheManager} to use to
	 * create a default {@link org.springframework.cache.interceptor.CacheResolver} if none
	 * is set already.
	 * If no resolver and no cache manager are set at the operation level, and no cache
	 * resolver is set via {@link #cacheResolver}, this one is used instead of the default.
	 * @see org.springframework.cache.interceptor.SimpleCacheResolver
	 */
	String cacheManager() default "";

	/**
	 * The bean name of the custom {@link org.springframework.cache.interceptor.CacheResolver} to use.
	 * If no resolver and no cache manager are set at the operation level, this one is used
	 * instead of the default.
	 */
	String cacheResolver() default "";

}
