

package org.springframework.http.converter;

import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;

/**
 * Thrown by {@link HttpMessageConverter} implementations when a conversion attempt fails.
 *
 * @author Arjen Poutsma
 * @author Sebastien Deleuze
 * @since 3.0
 */
@SuppressWarnings("serial")
public class HttpMessageConversionException extends NestedRuntimeException {

	/**
	 * Create a new HttpMessageConversionException.
	 * @param msg the detail message
	 */
	public HttpMessageConversionException(String msg) {
		super(msg);
	}

	/**
	 * Create a new HttpMessageConversionException.
	 * @param msg the detail message
	 * @param cause the root cause (if any)
	 */
	public HttpMessageConversionException(String msg, @Nullable Throwable cause) {
		super(msg, cause);
	}

}
