

package org.springframework.messaging.handler.invocation;

import org.springframework.core.MethodParameter;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * Support for {@link ListenableFuture} as a return value type.
 *
 * @author Sebastien Deleuze
 * @since 4.2
 */
public class ListenableFutureReturnValueHandler extends AbstractAsyncReturnValueHandler {

	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return ListenableFuture.class.isAssignableFrom(returnType.getParameterType());
	}

	@Override
	@SuppressWarnings("unchecked")
	public ListenableFuture<?> toListenableFuture(Object returnValue, MethodParameter returnType) {
		return (ListenableFuture<?>) returnValue;
	}

}
