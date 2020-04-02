

package org.springframework.messaging.handler.invocation;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * An extension of {@link HandlerMethodReturnValueHandler} for handling async,
 * Future-like return value types that support success and error callbacks.
 * Essentially anything that can be adapted to a {@link ListenableFuture}.
 *
 * Implementations should consider extending the convenient base class
 * {@link AbstractAsyncReturnValueHandler}.
 *
 * @author Rossen Stoyanchev
 * @since 4.2
 * @see AbstractAsyncReturnValueHandler
 */
public interface AsyncHandlerMethodReturnValueHandler extends HandlerMethodReturnValueHandler {

	/**
	 * Whether the return value represents an asynchronous, Future-like type
	 * with success and error callbacks. If this method returns {@code true},
	 * then {@link #toListenableFuture} is invoked next. If it returns
	 * {@code false}, then {@link #handleReturnValue} is called.
	 * <strong>Note:</strong> this method will only be invoked after
	 * {@link #supportsReturnType(org.springframework.core.MethodParameter)}
	 * is called and it returns {@code true}.
	 * @param returnValue the value returned from the handler method
	 * @param returnType the type of the return value
	 * @return {@code true} if the return value type represents an async value
	 */
	boolean isAsyncReturnValue(Object returnValue, MethodParameter returnType);

	/**
	 * Adapt the asynchronous return value to a {@link ListenableFuture}.
	 * Implementations should consider returning an instance of
	 * {@link org.springframework.util.concurrent.SettableListenableFuture
	 * SettableListenableFuture}. Return value handling will then continue when
	 * the ListenableFuture is completed with either success or error.
	 * <strong>Note:</strong> this method will only be invoked after
	 * {@link #supportsReturnType(org.springframework.core.MethodParameter)}
	 * is called and it returns {@code true}.
	 * @param returnValue the value returned from the handler method
	 * @param returnType the type of the return value
	 * @return the resulting ListenableFuture, or {@code null} in which case
	 * no further handling will be performed
	 */
	@Nullable
	ListenableFuture<?> toListenableFuture(Object returnValue, MethodParameter returnType);

}
