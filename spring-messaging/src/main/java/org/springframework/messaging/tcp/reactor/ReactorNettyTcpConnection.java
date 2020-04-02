

package org.springframework.messaging.tcp.reactor;

import io.netty.buffer.ByteBuf;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Mono;
import reactor.netty.NettyInbound;
import reactor.netty.NettyOutbound;

import org.springframework.messaging.Message;
import org.springframework.messaging.tcp.TcpConnection;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.MonoToListenableFutureAdapter;

/**
 * Reactor Netty based implementation of {@link TcpConnection}.
 *
 * @author Rossen Stoyanchev
 * @since 5.0
 * @param  the type of payload for outbound messages
 */
public class ReactorNettyTcpConnection implements TcpConnection {

	private final NettyInbound inbound;

	private final NettyOutbound outbound;

	private final ReactorNettyCodec codec;

	private final DirectProcessor<Void> closeProcessor;


	public ReactorNettyTcpConnection(NettyInbound inbound, NettyOutbound outbound,
			ReactorNettyCodec codec, DirectProcessor<Void> closeProcessor) {

		this.inbound = inbound;
		this.outbound = outbound;
		this.codec = codec;
		this.closeProcessor = closeProcessor;
	}


	@Override
	public ListenableFuture<Void> send(Message message) {
		ByteBuf byteBuf = this.outbound.alloc().buffer();
		this.codec.encode(message, byteBuf);
		Mono<Void> sendCompletion = this.outbound.send(Mono.just(byteBuf)).then();
		return new MonoToListenableFutureAdapter<>(sendCompletion);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void onReadInactivity(Runnable runnable, long inactivityDuration) {
		this.inbound.withConnection(conn -> conn.onReadIdle(inactivityDuration, runnable));
	}

	@Override
	@SuppressWarnings("deprecation")
	public void onWriteInactivity(Runnable runnable, long inactivityDuration) {
		this.inbound.withConnection(conn -> conn.onWriteIdle(inactivityDuration, runnable));
	}

	@Override
	public void close() {
		this.closeProcessor.onComplete();
	}

}
