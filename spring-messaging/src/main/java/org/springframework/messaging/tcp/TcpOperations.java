

package org.springframework.messaging.tcp;

import org.springframework.util.concurrent.ListenableFuture;

/**
 * A contract for establishing TCP connections.
 *
 *
 * @since 4.0
 * @param <P> the type of payload for in and outbound messages
 */
public interface TcpOperations<P> {

	/**
	 * Open a new connection.
	 * @param connectionHandler a handler to manage the connection
	 * @return a ListenableFuture that can be used to determine when and if the
	 * connection is successfully established
	 */
	ListenableFuture<Void> connect(TcpConnectionHandler<P> connectionHandler);

	/**
	 * Open a new connection and a strategy for reconnecting if the connection fails.
	 * @param connectionHandler a handler to manage the connection
	 * @param reconnectStrategy a strategy for reconnecting
	 * @return a ListenableFuture that can be used to determine when and if the
	 * initial connection is successfully established
	 */
	ListenableFuture<Void> connect(TcpConnectionHandler<P> connectionHandler, ReconnectStrategy reconnectStrategy);

	/**
	 * Shut down and close any open connections.
	 * @return a ListenableFuture that can be used to determine when and if the
	 * connection is successfully closed
	 */
	ListenableFuture<Void> shutdown();

}
