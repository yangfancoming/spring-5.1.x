

package org.springframework.messaging.handler.invocation;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.springframework.core.MethodParameter;
import org.springframework.util.concurrent.CompletableToListenableFutureAdapter;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * Support for {@link CompletableFuture} (and as of 4.3.7 also {@link CompletionStage})
 * as a return value type.
 *
 * @author Sebastien Deleuze

 * @since 4.2
 */
public class CompletableFutureReturnValueHandler extends AbstractAsyncReturnValueHandler {

	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return CompletionStage.class.isAssignableFrom(returnType.getParameterType());
	}

	@Override
	@SuppressWarnings("unchecked")
	public ListenableFuture<?> toListenableFuture(Object returnValue, MethodParameter returnType) {
		return new CompletableToListenableFutureAdapter<>((CompletionStage<Object>) returnValue);
	}

}
