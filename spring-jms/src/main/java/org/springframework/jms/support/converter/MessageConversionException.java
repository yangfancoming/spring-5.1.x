

package org.springframework.jms.support.converter;

import org.springframework.jms.JmsException;
import org.springframework.lang.Nullable;

/**
 * Thrown by {@link MessageConverter} implementations when the conversion
 * of an object to/from a {@link javax.jms.Message} fails.
 *
 * @author Mark Pollack
 * @since 1.1
 * @see MessageConverter
 */
@SuppressWarnings("serial")
public class MessageConversionException extends JmsException {

	/**
	 * Create a new MessageConversionException.
	 * @param msg the detail message
	 */
	public MessageConversionException(String msg) {
		super(msg);
	}

	/**
	 * Create a new MessageConversionException.
	 * @param msg the detail message
	 * @param cause the root cause (if any)
	 */
	public MessageConversionException(String msg, @Nullable Throwable cause) {
		super(msg, cause);
	}

}
