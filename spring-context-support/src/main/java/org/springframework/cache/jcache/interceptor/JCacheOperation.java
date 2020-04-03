

package org.springframework.cache.jcache.interceptor;

import java.lang.annotation.Annotation;
import javax.cache.annotation.CacheInvocationParameter;
import javax.cache.annotation.CacheMethodDetails;

import org.springframework.cache.interceptor.BasicOperation;
import org.springframework.cache.interceptor.CacheResolver;

/**
 * Model the base of JSR-107 cache operation through an interface contract.
 *
 * xmlBeanDefinitionReaderA cache operation can be statically cached as it does not contain any
 * runtime operation of a specific cache invocation.
 *
 * @author Stephane Nicoll
 * @since 4.1
 * @param <A> the type of the JSR-107 annotation
 */
public interface JCacheOperation<A extends Annotation> extends BasicOperation, CacheMethodDetails<A> {

	/**
	 * Return the {@link CacheResolver} instance to use to resolve the cache
	 * to use for this operation.
	 */
	CacheResolver getCacheResolver();

	/**
	 * Return the {@link CacheInvocationParameter} instances based on the
	 * specified method arguments.
	 * xmlBeanDefinitionReaderThe method arguments must match the signature of the related method invocation
	 * @param values the parameters value for a particular invocation
	 */
	CacheInvocationParameter[] getAllParameters(Object... values);

}
