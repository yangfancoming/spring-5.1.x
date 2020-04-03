

package org.springframework.cache.jcache.interceptor;

import java.lang.reflect.Method;
import java.util.List;
import javax.cache.annotation.CacheInvocationParameter;
import javax.cache.annotation.CacheMethodDetails;
import javax.cache.annotation.CachePut;

import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.lang.Nullable;
import org.springframework.util.ExceptionTypeFilter;

/**
 * The {@link JCacheOperation} implementation for a {@link CachePut} operation.
 *
 * @author Stephane Nicoll
 * @since 4.1
 * @see CachePut
 */
class CachePutOperation extends AbstractJCacheKeyOperation<CachePut> {

	private final ExceptionTypeFilter exceptionTypeFilter;

	private final CacheParameterDetail valueParameterDetail;


	public CachePutOperation(
			CacheMethodDetails<CachePut> methodDetails, CacheResolver cacheResolver, KeyGenerator keyGenerator) {

		super(methodDetails, cacheResolver, keyGenerator);

		CachePut ann = methodDetails.getCacheAnnotation();
		this.exceptionTypeFilter = createExceptionTypeFilter(ann.cacheFor(), ann.noCacheFor());

		CacheParameterDetail valueParameterDetail =
				initializeValueParameterDetail(methodDetails.getMethod(), this.allParameterDetails);
		if (valueParameterDetail == null) {
			throw new IllegalArgumentException("No parameter annotated with @CacheValue was found for " +
					methodDetails.getMethod());
		}
		this.valueParameterDetail = valueParameterDetail;
	}


	@Override
	public ExceptionTypeFilter getExceptionTypeFilter() {
		return this.exceptionTypeFilter;
	}

	/**
	 * Specify if the cache should be updated before invoking the method. By default,
	 * the cache is updated after the method invocation.
	 * @see javax.cache.annotation.CachePut#afterInvocation()
	 */
	public boolean isEarlyPut() {
		return !getCacheAnnotation().afterInvocation();
	}

	/**
	 * Return the {@link CacheInvocationParameter} for the parameter holding the value
	 * to cache.
	 * xmlBeanDefinitionReaderThe method arguments must match the signature of the related method invocation
	 * @param values the parameters value for a particular invocation
	 * @return the {@link CacheInvocationParameter} instance for the value parameter
	 */
	public CacheInvocationParameter getValueParameter(Object... values) {
		int parameterPosition = this.valueParameterDetail.getParameterPosition();
		if (parameterPosition >= values.length) {
			throw new IllegalStateException("Values mismatch, value parameter at position " +
					parameterPosition + " cannot be matched against " + values.length + " value(s)");
		}
		return this.valueParameterDetail.toCacheInvocationParameter(values[parameterPosition]);
	}


	@Nullable
	private static CacheParameterDetail initializeValueParameterDetail(
			Method method, List<CacheParameterDetail> allParameters) {

		CacheParameterDetail result = null;
		for (CacheParameterDetail parameter : allParameters) {
			if (parameter.isValue()) {
				if (result == null) {
					result = parameter;
				}
				else {
					throw new IllegalArgumentException("More than one @CacheValue found on " + method + "");
				}
			}
		}
		return result;
	}

}
