

package org.springframework.web.socket.messaging;

import java.security.Principal;

import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;

/**
 * Event raised when a new WebSocket client using a Simple Messaging Protocol
 * (e.g. STOMP) as the WebSocket sub-protocol issues a connect request.
 *
 * xmlBeanDefinitionReaderNote that this is not the same as the WebSocket session getting established
 * but rather the client's first attempt to connect within the sub-protocol,
 * for example sending the STOMP CONNECT frame.
 *
 *
 * @since 4.0.3
 */
@SuppressWarnings("serial")
public class SessionConnectEvent extends AbstractSubProtocolEvent {

	/**
	 * Create a new SessionConnectEvent.
	 * @param source the component that published the event (never {@code null})
	 * @param message the connect message
	 */
	public SessionConnectEvent(Object source, Message<byte[]> message) {
		super(source, message);
	}

	public SessionConnectEvent(Object source, Message<byte[]> message, @Nullable Principal user) {
		super(source, message, user);
	}

}
