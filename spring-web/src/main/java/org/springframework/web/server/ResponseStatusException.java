

package org.springframework.web.server;

import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Base class for exceptions associated with specific HTTP response status codes.
 *
 *

 * @since 5.0
 */
@SuppressWarnings("serial")
public class ResponseStatusException extends NestedRuntimeException {

	private final HttpStatus status;

	@Nullable
	private final String reason;


	/**
	 * Constructor with a response status.
	 * @param status the HTTP status (required)
	 */
	public ResponseStatusException(HttpStatus status) {
		this(status, null, null);
	}

	/**
	 * Constructor with a response status and a reason to add to the exception
	 * message as explanation.
	 * @param status the HTTP status (required)
	 * @param reason the associated reason (optional)
	 */
	public ResponseStatusException(HttpStatus status, @Nullable String reason) {
		this(status, reason, null);
	}

	/**
	 * Constructor with a response status and a reason to add to the exception
	 * message as explanation, as well as a nested exception.
	 * @param status the HTTP status (required)
	 * @param reason the associated reason (optional)
	 * @param cause a nested exception (optional)
	 */
	public ResponseStatusException(HttpStatus status, @Nullable String reason, @Nullable Throwable cause) {
		super(null, cause);
		Assert.notNull(status, "HttpStatus is required");
		this.status = status;
		this.reason = reason;
	}


	/**
	 * The HTTP status that fits the exception (never {@code null}).
	 */
	public HttpStatus getStatus() {
		return this.status;
	}

	/**
	 * The reason explaining the exception (potentially {@code null} or empty).
	 */
	@Nullable
	public String getReason() {
		return this.reason;
	}

	@Override
	public String getMessage() {
		String msg = this.status + (this.reason != null ? " \"" + this.reason + "\"" : "");
		return NestedExceptionUtils.buildMessage(msg, getCause());
	}

}
