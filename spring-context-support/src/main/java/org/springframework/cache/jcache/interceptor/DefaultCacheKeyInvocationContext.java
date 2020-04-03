

package org.springframework.cache.jcache.interceptor;

import java.lang.annotation.Annotation;
import javax.cache.annotation.CacheInvocationParameter;
import javax.cache.annotation.CacheKeyInvocationContext;

import org.springframework.lang.Nullable;

/**
 * The default {@link CacheKeyInvocationContext} implementation.
 *
 * @author Stephane Nicoll
 * @since 4.1
 * @param <A> the annotation type
 */
class DefaultCacheKeyInvocationContext<A extends Annotation> extends DefaultCacheInvocationContext<A>
		implements CacheKeyInvocationContext<A> {

	private final CacheInvocationParameter[] keyParameters;

	@Nullable
	private final CacheInvocationParameter valueParameter;


	public DefaultCacheKeyInvocationContext(AbstractJCacheKeyOperation<A> operation, Object target, Object[] args) {
		super(operation, target, args);
		this.keyParameters = operation.getKeyParameters(args);
		if (operation instanceof CachePutOperation) {
			this.valueParameter = ((CachePutOperation) operation).getValueParameter(args);
		}
		else {
			this.valueParameter = null;
		}
	}


	@Override
	public CacheInvocationParameter[] getKeyParameters() {
		return this.keyParameters.clone();
	}

	@Override
	@Nullable
	public CacheInvocationParameter getValueParameter() {
		return this.valueParameter;
	}

}
