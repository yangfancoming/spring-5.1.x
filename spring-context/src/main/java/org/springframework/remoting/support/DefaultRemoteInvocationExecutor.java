

package org.springframework.remoting.support;

import java.lang.reflect.InvocationTargetException;

import org.springframework.util.Assert;

/**
 * Default implementation of the {@link RemoteInvocationExecutor} interface.
 * Simply delegates to {@link RemoteInvocation}'s invoke method.
 *

 * @since 1.1
 * @see RemoteInvocation#invoke
 */
public class DefaultRemoteInvocationExecutor implements RemoteInvocationExecutor {

	@Override
	public Object invoke(RemoteInvocation invocation, Object targetObject)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException{

		Assert.notNull(invocation, "RemoteInvocation must not be null");
		Assert.notNull(targetObject, "Target object must not be null");
		return invocation.invoke(targetObject);
	}

}
