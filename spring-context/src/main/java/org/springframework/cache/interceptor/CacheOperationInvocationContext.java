

package org.springframework.cache.interceptor;

import java.lang.reflect.Method;

/**
 * Representation of the context of the invocation of a cache operation.
 *
 * <p>The cache operation is static and independent of a particular invocation;
 * this interface gathers the operation and a particular invocation.
 *
 * @author Stephane Nicoll
 * @since 4.1
 * @param <O> the operation type
 */
public interface CacheOperationInvocationContext<O extends BasicOperation> {

	/**
	 * Return the cache operation.
	 */
	O getOperation();

	/**
	 * Return the target instance on which the method was invoked.
	 */
	Object getTarget();

	/**
	 * Return the method which was invoked.
	 */
	Method getMethod();

	/**
	 * Return the argument list used to invoke the method.
	 */
	Object[] getArgs();

}
