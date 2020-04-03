

package org.springframework.web.socket.messaging;

import java.security.Principal;

import org.springframework.context.ApplicationEvent;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;

/**
 * A base class for events for a message received from a WebSocket client and
 * parsed into a higher-level sub-protocol (e.g. STOMP).
 *
 * @author Rossen Stoyanchev
 * @since 4.1
 */
@SuppressWarnings("serial")
public abstract class AbstractSubProtocolEvent extends ApplicationEvent {

	private final Message<byte[]> message;

	@Nullable
	private final Principal user;


	/**
	 * Create a new AbstractSubProtocolEvent.
	 * @param source the component that published the event (never {@code null})
	 * @param message the incoming message (never {@code null})
	 */
	protected AbstractSubProtocolEvent(Object source, Message<byte[]> message) {
		this(source, message, null);
	}

	/**
	 * Create a new AbstractSubProtocolEvent.
	 * @param source the component that published the event (never {@code null})
	 * @param message the incoming message (never {@code null})
	 */
	protected AbstractSubProtocolEvent(Object source, Message<byte[]> message, @Nullable Principal user) {
		super(source);
		Assert.notNull(message, "Message must not be null");
		this.message = message;
		this.user = user;
	}


	/**
	 * Return the Message associated with the event. Here is an example of
	 * obtaining information about the session id or any headers in the
	 * message:
	 * <pre class="code">
	 * StompHeaderAccessor headers = StompHeaderAccessor.wrap(message);
	 * headers.getSessionId();
	 * headers.getSessionAttributes();
	 * headers.getPrincipal();
	 * </pre>
	 */
	public Message<byte[]> getMessage() {
		return this.message;
	}

	/**
	 * Return the user for the session associated with the event.
	 */
	@Nullable
	public Principal getUser() {
		return this.user;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + this.message + "]";
	}

}
