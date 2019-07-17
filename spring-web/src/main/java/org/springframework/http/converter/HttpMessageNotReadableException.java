

package org.springframework.http.converter;

import org.springframework.http.HttpInputMessage;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Thrown by {@link HttpMessageConverter} implementations when the
 * {@link HttpMessageConverter#read} method fails.
 *
 * @author Arjen Poutsma

 * @since 3.0
 */
@SuppressWarnings("serial")
public class HttpMessageNotReadableException extends HttpMessageConversionException {

	@Nullable
	private final HttpInputMessage httpInputMessage;


	/**
	 * Create a new HttpMessageNotReadableException.
	 * @param msg the detail message
	 * @deprecated as of 5.1, in favor of {@link #HttpMessageNotReadableException(String, HttpInputMessage)}
	 */
	@Deprecated
	public HttpMessageNotReadableException(String msg) {
		super(msg);
		this.httpInputMessage = null;
	}

	/**
	 * Create a new HttpMessageNotReadableException.
	 * @param msg the detail message
	 * @param cause the root cause (if any)
	 * @deprecated as of 5.1, in favor of {@link #HttpMessageNotReadableException(String, Throwable, HttpInputMessage)}
	 */
	@Deprecated
	public HttpMessageNotReadableException(String msg, @Nullable Throwable cause) {
		super(msg, cause);
		this.httpInputMessage = null;
	}

	/**
	 * Create a new HttpMessageNotReadableException.
	 * @param msg the detail message
	 * @param httpInputMessage the original HTTP message
	 * @since 5.1
	 */
	public HttpMessageNotReadableException(String msg, HttpInputMessage httpInputMessage) {
		super(msg);
		this.httpInputMessage = httpInputMessage;
	}

	/**
	 * Create a new HttpMessageNotReadableException.
	 * @param msg the detail message
	 * @param cause the root cause (if any)
	 * @param httpInputMessage the original HTTP message
	 * @since 5.1
	 */
	public HttpMessageNotReadableException(String msg, @Nullable Throwable cause, HttpInputMessage httpInputMessage) {
		super(msg, cause);
		this.httpInputMessage = httpInputMessage;
	}


	/**
	 * Return the original HTTP message.
	 * @since 5.1
	 */
	public HttpInputMessage getHttpInputMessage() {
		Assert.state(this.httpInputMessage != null, "No HttpInputMessage available - use non-deprecated constructors");
		return this.httpInputMessage;
	}

}
