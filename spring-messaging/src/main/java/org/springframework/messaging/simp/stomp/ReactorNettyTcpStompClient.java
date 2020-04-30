

package org.springframework.messaging.simp.stomp;

import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.SimpLogging;
import org.springframework.messaging.tcp.TcpOperations;
import org.springframework.messaging.tcp.reactor.ReactorNettyTcpClient;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * A STOMP over TCP client that uses {@link ReactorNettyTcpClient}.
 *
 *
 * @since 5.0
 */
public class ReactorNettyTcpStompClient extends StompClientSupport {

	private final TcpOperations<byte[]> tcpClient;


	/**
	 * Create an instance with host "127.0.0.1" and port 61613.
	 */
	public ReactorNettyTcpStompClient() {
		this("127.0.0.1", 61613);
	}

	/**
	 * Create an instance with the given host and port.
	 * @param host the host
	 * @param port the port
	 */
	public ReactorNettyTcpStompClient(String host, int port) {
		this.tcpClient = initTcpClient(host, port);
	}

	/**
	 * Create an instance with a pre-configured TCP client.
	 * @param tcpClient the client to use
	 */
	public ReactorNettyTcpStompClient(TcpOperations<byte[]> tcpClient) {
		Assert.notNull(tcpClient, "'tcpClient' is required");
		this.tcpClient = tcpClient;
	}

	private static ReactorNettyTcpClient<byte[]> initTcpClient(String host, int port) {
		ReactorNettyTcpClient<byte[]> client = new ReactorNettyTcpClient<>(host, port, new StompReactorNettyCodec());
		client.setLogger(SimpLogging.forLog(client.getLogger()));
		return client;
	}


	/**
	 * Connect and notify the given {@link StompSessionHandler} when connected
	 * on the STOMP level.
	 * @param handler the handler for the STOMP session
	 * @return a ListenableFuture for access to the session when ready for use
	 */
	public ListenableFuture<StompSession> connect(StompSessionHandler handler) {
		return connect(null, handler);
	}

	/**
	 * An overloaded version of {@link #connect(StompSessionHandler)} that
	 * accepts headers to use for the STOMP CONNECT frame.
	 * @param connectHeaders headers to add to the CONNECT frame
	 * @param handler the handler for the STOMP session
	 * @return a ListenableFuture for access to the session when ready for use
	 */
	public ListenableFuture<StompSession> connect(@Nullable StompHeaders connectHeaders, StompSessionHandler handler) {
		ConnectionHandlingStompSession session = createSession(connectHeaders, handler);
		this.tcpClient.connect(session);
		return session.getSessionFuture();
	}

	/**
	 * Shut down the client and release resources.
	 */
	public void shutdown() {
		this.tcpClient.shutdown();
	}

	@Override
	public String toString() {
		return "ReactorNettyTcpStompClient[" + this.tcpClient + "]";
	}
}
