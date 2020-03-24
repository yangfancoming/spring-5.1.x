

package org.springframework.messaging.converter;

import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;

/**
 * An exception raised by {@link MessageConverter} implementations.
 *
 * @author Mark Fisher
 * @since 4.0
 */
@SuppressWarnings("serial")
public class MessageConversionException extends MessagingException {

	public MessageConversionException(String description) {
		super(description);
	}

	public MessageConversionException(@Nullable String description, @Nullable Throwable cause) {
		super(description, cause);
	}

	public MessageConversionException(Message<?> failedMessage, String description) {
		super(failedMessage, description);
	}

	public MessageConversionException(Message<?> failedMessage, @Nullable String description, @Nullable Throwable cause) {
		super(failedMessage, description, cause);
	}

}
