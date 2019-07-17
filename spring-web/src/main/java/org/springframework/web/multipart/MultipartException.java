

package org.springframework.web.multipart;

import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;

/**
 * Exception thrown when multipart resolution fails.
 *
 * @author Trevor D. Cook

 * @since 29.09.2003
 * @see MultipartResolver#resolveMultipart
 * @see org.springframework.web.multipart.support.MultipartFilter
 */
@SuppressWarnings("serial")
public class MultipartException extends NestedRuntimeException {

	/**
	 * Constructor for MultipartException.
	 * @param msg the detail message
	 */
	public MultipartException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for MultipartException.
	 * @param msg the detail message
	 * @param cause the root cause from the multipart parsing API in use
	 */
	public MultipartException(String msg, @Nullable Throwable cause) {
		super(msg, cause);
	}

}
