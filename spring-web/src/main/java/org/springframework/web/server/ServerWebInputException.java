

package org.springframework.web.server;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

/**
 * Exception for errors that fit response status 400 (bad request) for use in
 * Spring Web applications. The exception provides additional fields (e.g.
 * an optional {@link MethodParameter} if related to the error).
 *
 *
 * @since 5.0
 */
@SuppressWarnings("serial")
public class ServerWebInputException extends ResponseStatusException {

	@Nullable
	private final MethodParameter parameter;


	/**
	 * Constructor with an explanation only.
	 */
	public ServerWebInputException(String reason) {
		this(reason, null, null);
	}

	/**
	 * Constructor for a 400 error linked to a specific {@code MethodParameter}.
	 */
	public ServerWebInputException(String reason, @Nullable MethodParameter parameter) {
		this(reason, parameter, null);
	}

	/**
	 * Constructor for a 400 error with a root cause.
	 */
	public ServerWebInputException(String reason, @Nullable MethodParameter parameter, @Nullable Throwable cause) {
		super(HttpStatus.BAD_REQUEST, reason, cause);
		this.parameter = parameter;
	}


	/**
	 * Return the {@code MethodParameter} associated with this error, if any.
	 */
	@Nullable
	public MethodParameter getMethodParameter() {
		return this.parameter;
	}

}
