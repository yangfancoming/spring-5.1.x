

package org.springframework.web.reactive.function.client;

import org.springframework.core.NestedRuntimeException;

/**
 * Abstract base class for exception published by {@link WebClient} in case of errors.
 *
 * @author Arjen Poutsma
 * @since 5.0
 */
public abstract class WebClientException extends NestedRuntimeException {

	private static final long serialVersionUID = 472776714118912855L;

	/**
	 * Construct a new instance of {@code WebClientException} with the given message.
	 * @param msg the message
	 */
	public WebClientException(String msg) {
		super(msg);
	}

	/**
	 * Construct a new instance of {@code WebClientException} with the given message
	 * and exception.
	 * @param msg the message
	 * @param ex the exception
	 */
	public WebClientException(String msg, Throwable ex) {
		super(msg, ex);
	}

}
