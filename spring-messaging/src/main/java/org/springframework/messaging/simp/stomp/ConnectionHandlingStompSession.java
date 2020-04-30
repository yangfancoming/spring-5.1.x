

package org.springframework.messaging.simp.stomp;

import org.springframework.messaging.tcp.TcpConnectionHandler;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * A {@link StompSession} that implements
 * {@link org.springframework.messaging.tcp.TcpConnectionHandler
 * TcpConnectionHandler} in order to send and receive messages.
 *
 * A ConnectionHandlingStompSession can be used with any TCP or WebSocket
 * library that is adapted to the {@code TcpConnectionHandler} contract.
 *
 *
 * @since 4.2
 */
public interface ConnectionHandlingStompSession extends StompSession, TcpConnectionHandler<byte[]> {

	/**
	 * Return a future that will complete when the session is ready for use.
	 */
	ListenableFuture<StompSession> getSessionFuture();

}
