

package org.springframework.messaging.simp.stomp;

/**
 * Raised when the connection for a STOMP session is lost rather than closed.
 *
 *
 * @since 4.2
 */
@SuppressWarnings("serial")
public class ConnectionLostException extends RuntimeException {

	public ConnectionLostException(String message) {
		super(message);
	}

}
