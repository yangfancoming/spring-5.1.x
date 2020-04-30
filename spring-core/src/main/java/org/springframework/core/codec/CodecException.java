

package org.springframework.core.codec;

import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;

/**
 * General error that indicates a problem while encoding and decoding to and
 * from an Object stream.
 *
 * @author Sebastien Deleuze
 *
 * @since 5.0
 */
@SuppressWarnings("serial")
public class CodecException extends NestedRuntimeException {

	/**
	 * Create a new CodecException.
	 * @param msg the detail message
	 */
	public CodecException(String msg) {
		super(msg);
	}

	/**
	 * Create a new CodecException.
	 * @param msg the detail message
	 * @param cause root cause for the exception, if any
	 */
	public CodecException(String msg, @Nullable Throwable cause) {
		super(msg, cause);
	}

}
