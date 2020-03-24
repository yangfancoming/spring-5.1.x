

package org.springframework.messaging;

/**
 * Contract for handling a {@link Message}.
 *
 * @author Mark Fisher
 * @author Iwein Fuld
 * @since 4.0
 */
@FunctionalInterface
public interface MessageHandler {

	/**
	 * Handle the given message.
	 * @param message the message to be handled
	 * @throws MessagingException if the handler failed to process the message
	 */
	void handleMessage(Message<?> message) throws MessagingException;

}
