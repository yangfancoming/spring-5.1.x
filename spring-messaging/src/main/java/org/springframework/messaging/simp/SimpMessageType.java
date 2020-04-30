

package org.springframework.messaging.simp;

/**
 * A generic representation of different kinds of messages found in simple messaging
 * protocols like STOMP.
 *
 *
 * @since 4.0
 */
public enum SimpMessageType {

	CONNECT,

	CONNECT_ACK,

	MESSAGE,

	SUBSCRIBE,

	UNSUBSCRIBE,

	HEARTBEAT,

	DISCONNECT,

	DISCONNECT_ACK,

	OTHER;

}
