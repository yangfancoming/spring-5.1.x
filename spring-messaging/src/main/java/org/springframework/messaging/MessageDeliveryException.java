

package org.springframework.messaging;

/**
 * Exception that indicates an error occurred during message delivery.
 *
 * @author Mark Fisher
 * @since 4.0
 */
@SuppressWarnings("serial")
public class MessageDeliveryException extends MessagingException {

	public MessageDeliveryException(String description) {
		super(description);
	}

	public MessageDeliveryException(Message<?> undeliveredMessage) {
		super(undeliveredMessage);
	}

	public MessageDeliveryException(Message<?> undeliveredMessage, String description) {
		super(undeliveredMessage, description);
	}

	public MessageDeliveryException(Message<?> message, Throwable cause) {
		super(message, cause);
	}

	public MessageDeliveryException(Message<?> undeliveredMessage, String description, Throwable cause) {
		super(undeliveredMessage, description, cause);
	}

}
