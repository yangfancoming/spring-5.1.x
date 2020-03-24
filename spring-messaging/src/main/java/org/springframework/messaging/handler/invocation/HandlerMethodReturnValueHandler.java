

package org.springframework.messaging.handler.invocation;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;

/**
 * Strategy interface to handle the value returned from the invocation of a
 * method handling a {@link Message}.
 *
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public interface HandlerMethodReturnValueHandler {

	/**
	 * Whether the given {@linkplain MethodParameter method return type} is
	 * supported by this handler.
	 * @param returnType the method return type to check
	 * @return {@code true} if this handler supports the supplied return type;
	 * {@code false} otherwise
	 */
	boolean supportsReturnType(MethodParameter returnType);

	/**
	 * Handle the given return value.
	 * @param returnValue the value returned from the handler method
	 * @param returnType the type of the return value. This type must have previously
	 * been passed to {@link #supportsReturnType(org.springframework.core.MethodParameter)}
	 * and it must have returned {@code true}.
	 * @param message the message that caused this method to be called
	 * @throws Exception if the return value handling results in an error
	 */
	void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType, Message<?> message)
			throws Exception;

}
