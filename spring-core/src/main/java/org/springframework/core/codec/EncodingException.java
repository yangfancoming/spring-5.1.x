
package org.springframework.core.codec;

import org.springframework.lang.Nullable;

/**
 * Indicates an issue with encoding the input Object stream with a focus on
 * not being able to encode Objects. As opposed to a more general I/O errors
 * or a {@link CodecException} such as a configuration issue that an
 * {@link Encoder} may also choose to raise.
 * @since 5.0
 * @see Encoder
 */
@SuppressWarnings("serial")
public class EncodingException extends CodecException {

	/**
	 * Create a new EncodingException.
	 * @param msg the detail message
	 */
	public EncodingException(String msg) {
		super(msg);
	}

	/**
	 * Create a new EncodingException.
	 * @param msg the detail message
	 * @param cause root cause for the exception, if any
	 */
	public EncodingException(String msg, @Nullable Throwable cause) {
		super(msg, cause);
	}

}
