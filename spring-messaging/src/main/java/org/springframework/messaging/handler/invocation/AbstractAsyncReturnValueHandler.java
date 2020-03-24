

package org.springframework.messaging.handler.invocation;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;

/**
 * Convenient base class for {@link AsyncHandlerMethodReturnValueHandler}
 * implementations that support only asynchronous (Future-like) return values
 * and merely serve as adapters of such types to Spring's
 * {@link org.springframework.util.concurrent.ListenableFuture ListenableFuture}.
 *
 * @author Sebastien Deleuze
 * @since 4.2
 */
public abstract class AbstractAsyncReturnValueHandler implements AsyncHandlerMethodReturnValueHandler {

	@Override
	public boolean isAsyncReturnValue(Object returnValue, MethodParameter returnType) {
		return true;
	}

	@Override
	public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType, Message<?> message) {
		// Should never be called since we return "true" from isAsyncReturnValue
		throw new IllegalStateException("Unexpected invocation");
	}

}
