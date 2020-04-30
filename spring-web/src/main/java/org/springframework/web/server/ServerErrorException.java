

package org.springframework.web.server;

import java.lang.reflect.Method;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

/**
 * Exception for an {@link HttpStatus#INTERNAL_SERVER_ERROR} that exposes extra
 * information about a controller method that failed, or a controller method
 * argument that could not be resolved.
 *
 *
 * @since 5.0
 */
@SuppressWarnings("serial")
public class ServerErrorException extends ResponseStatusException {

	@Nullable
	private final Method handlerMethod;

	@Nullable
	private final MethodParameter parameter;


	/**
	 * Constructor for a 500 error with a reason and an optional cause.
	 * @since 5.0.5
	 */
	public ServerErrorException(String reason, @Nullable Throwable cause) {
		super(HttpStatus.INTERNAL_SERVER_ERROR, reason, cause);
		this.handlerMethod = null;
		this.parameter = null;
	}

	/**
	 * Constructor for a 500 error with a handler {@link Method} and an optional cause.
	 * @since 5.0.5
	 */
	public ServerErrorException(String reason, Method handlerMethod, @Nullable Throwable cause) {
		super(HttpStatus.INTERNAL_SERVER_ERROR, reason, cause);
		this.handlerMethod = handlerMethod;
		this.parameter = null;
	}

	/**
	 * Constructor for a 500 error with a {@link MethodParameter} and an optional cause.
	 */
	public ServerErrorException(String reason, MethodParameter parameter, @Nullable Throwable cause) {
		super(HttpStatus.INTERNAL_SERVER_ERROR, reason, cause);
		this.handlerMethod = parameter.getMethod();
		this.parameter = parameter;
	}

	/**
	 * Constructor for a 500 error linked to a specific {@code MethodParameter}.
	 * @deprecated in favor of {@link #ServerErrorException(String, MethodParameter, Throwable)}
	 */
	@Deprecated
	public ServerErrorException(String reason, MethodParameter parameter) {
		this(reason, parameter, null);
	}

	/**
	 * Constructor for a 500 error with a reason only.
	 * @deprecated in favor of {@link #ServerErrorException(String, Throwable)}
	 */
	@Deprecated
	public ServerErrorException(String reason) {
		super(HttpStatus.INTERNAL_SERVER_ERROR, reason, null);
		this.handlerMethod = null;
		this.parameter = null;
	}


	/**
	 * Return the handler method associated with the error, if any.
	 * @since 5.0.5
	 */
	@Nullable
	public Method getHandlerMethod() {
		return this.handlerMethod;
	}

	/**
	 * Return the specific method parameter associated with the error, if any.
	 */
	@Nullable
	public MethodParameter getMethodParameter() {
		return this.parameter;
	}

}
