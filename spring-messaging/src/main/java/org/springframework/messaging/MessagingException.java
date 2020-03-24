

package org.springframework.messaging;

import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;

/**
 * The base exception for any failures related to messaging.
 *
 * @author Mark Fisher
 * @author Gary Russell
 * @since 4.0
 */
@SuppressWarnings("serial")
public class MessagingException extends NestedRuntimeException {

	@Nullable
	private final Message<?> failedMessage;


	public MessagingException(Message<?> message) {
		super(null, null);
		this.failedMessage = message;
	}

	public MessagingException(String description) {
		super(description);
		this.failedMessage = null;
	}

	public MessagingException(@Nullable String description, @Nullable Throwable cause) {
		super(description, cause);
		this.failedMessage = null;
	}

	public MessagingException(Message<?> message, String description) {
		super(description);
		this.failedMessage = message;
	}

	public MessagingException(Message<?> message, Throwable cause) {
		super(null, cause);
		this.failedMessage = message;
	}

	public MessagingException(Message<?> message, @Nullable String description, @Nullable Throwable cause) {
		super(description, cause);
		this.failedMessage = message;
	}


	@Nullable
	public Message<?> getFailedMessage() {
		return this.failedMessage;
	}

	@Override
	public String toString() {
		return super.toString() + (this.failedMessage == null ? ""
				: (", failedMessage=" + this.failedMessage));
	}

}
