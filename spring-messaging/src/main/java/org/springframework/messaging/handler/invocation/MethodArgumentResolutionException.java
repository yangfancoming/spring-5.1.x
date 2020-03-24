

package org.springframework.messaging.handler.invocation;

import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;

/**
 * Common exception resulting from the invocation of
 * {@link org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver}.
 *

 * @since 4.3.6
 * @see HandlerMethodArgumentResolver
 */
@SuppressWarnings("serial")
public class MethodArgumentResolutionException extends MessagingException {

	private final MethodParameter parameter;


	/**
	 * Create a new instance providing the invalid {@code MethodParameter}.
	 */
	public MethodArgumentResolutionException(Message<?> message, MethodParameter parameter) {
		super(message, getMethodParameterMessage(parameter));
		this.parameter = parameter;
	}

	/**
	 * Create a new instance providing the invalid {@code MethodParameter} and
	 * a prepared description.
	 */
	public MethodArgumentResolutionException(Message<?> message, MethodParameter parameter, String description) {
		super(message, getMethodParameterMessage(parameter) + ": " + description);
		this.parameter = parameter;
	}


	/**
	 * Return the MethodParameter that was rejected.
	 */
	public final MethodParameter getMethodParameter() {
		return this.parameter;
	}


	private static String getMethodParameterMessage(MethodParameter parameter) {
		return "Could not resolve method parameter at index " + parameter.getParameterIndex() +
				" in " + parameter.getExecutable().toGenericString();
	}

}
