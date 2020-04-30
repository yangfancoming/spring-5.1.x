

package org.springframework.messaging.tcp;

import java.io.Closeable;

import org.springframework.messaging.Message;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * A contract for sending messages and managing a TCP connection.
 *
 *
 * @since 4.0
 * @param <P> the type of payload for outbound {@link Message Messages}
 */
public interface TcpConnection<P> extends Closeable {

	/**
	 * Send the given message.
	 * @param message the message
	 * @return a ListenableFuture that can be used to determine when and if the
	 * message was successfully sent
	 */
	ListenableFuture<Void> send(Message<P> message);

	/**
	 * Register a task to invoke after a period of read inactivity.
	 * @param runnable the task to invoke
	 * @param duration the amount of inactive time in milliseconds
	 */
	void onReadInactivity(Runnable runnable, long duration);

	/**
	 * Register a task to invoke after a period of write inactivity.
	 * @param runnable the task to invoke
	 * @param duration the amount of inactive time in milliseconds
	 */
	void onWriteInactivity(Runnable runnable, long duration);

	/**
	 * Close the connection.
	 */
	@Override
	void close();

}
