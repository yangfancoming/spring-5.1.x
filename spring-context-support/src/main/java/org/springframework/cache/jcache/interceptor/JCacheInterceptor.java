

package org.springframework.cache.jcache.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.function.Supplier;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.lang.Nullable;
import org.springframework.util.function.SingletonSupplier;

/**
 * AOP Alliance MethodInterceptor for declarative cache
 * management using JSR-107 caching annotations.
 *
 * xmlBeanDefinitionReaderDerives from the {@link JCacheAspectSupport} class which
 * contains the integration with Spring's underlying caching API.
 * JCacheInterceptor simply calls the relevant superclass method.
 *
 * xmlBeanDefinitionReaderJCacheInterceptors are thread-safe.
 *
 * @author Stephane Nicoll

 * @since 4.1
 * @see org.springframework.cache.interceptor.CacheInterceptor
 */
@SuppressWarnings("serial")
public class JCacheInterceptor extends JCacheAspectSupport implements MethodInterceptor, Serializable {

	/**
	 * Construct a new {@code JCacheInterceptor} with the default error handler.
	 */
	public JCacheInterceptor() {
	}

	/**
	 * Construct a new {@code JCacheInterceptor} with the given error handler.
	 * @param errorHandler a supplier for the error handler to use,
	 * applying the default error handler if the supplier is not resolvable
	 * @since 5.1
	 */
	public JCacheInterceptor(@Nullable Supplier<CacheErrorHandler> errorHandler) {
		this.errorHandler = new SingletonSupplier<>(errorHandler, SimpleCacheErrorHandler::new);
	}


	@Override
	@Nullable
	public Object invoke(final MethodInvocation invocation) throws Throwable {
		Method method = invocation.getMethod();

		CacheOperationInvoker aopAllianceInvoker = () -> {
			try {
				return invocation.proceed();
			}
			catch (Throwable ex) {
				throw new CacheOperationInvoker.ThrowableWrapper(ex);
			}
		};

		try {
			return execute(aopAllianceInvoker, invocation.getThis(), method, invocation.getArguments());
		}
		catch (CacheOperationInvoker.ThrowableWrapper th) {
			throw th.getOriginal();
		}
	}

}
