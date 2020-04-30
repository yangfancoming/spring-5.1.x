

package org.springframework.messaging.simp.annotation.support;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;

/**
 * {@link MessagingException} thrown when a session is missing.
 *
 *
 * @since 4.0
 */
@SuppressWarnings("serial")
public class MissingSessionUserException extends MessagingException {

	public MissingSessionUserException(Message<?> message) {
		super(message, "No \"user\" header in message");
	}

}
