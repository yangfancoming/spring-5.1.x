

package org.springframework.remoting.support;

import org.aopalliance.intercept.MethodInvocation;

/**
 * Strategy interface for creating a {@link RemoteInvocation} from an AOP Alliance
 * {@link org.aopalliance.intercept.MethodInvocation}.
 *
 * Used by {@link org.springframework.remoting.rmi.RmiClientInterceptor} (for RMI invokers)
 * and by {@link org.springframework.remoting.httpinvoker.HttpInvokerClientInterceptor}.
 *

 * @since 1.1
 * @see DefaultRemoteInvocationFactory
 * @see org.springframework.remoting.rmi.RmiClientInterceptor#setRemoteInvocationFactory
 * @see org.springframework.remoting.httpinvoker.HttpInvokerClientInterceptor#setRemoteInvocationFactory
 */
public interface RemoteInvocationFactory {

	/**
	 * Create a serializable RemoteInvocation object from the given AOP
	 * MethodInvocation.
	 * Can be implemented to add custom context information to the
	 * remote invocation, for example user credentials.
	 * @param methodInvocation the original AOP MethodInvocation object
	 * @return the RemoteInvocation object
	 */
	RemoteInvocation createRemoteInvocation(MethodInvocation methodInvocation);

}
