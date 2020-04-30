

package org.springframework.web.reactive.socket.server.upgrade;

import java.util.function.Supplier;

import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerResponse;

import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.server.reactive.AbstractServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.adapter.NettyWebSocketSessionSupport;
import org.springframework.web.reactive.socket.adapter.ReactorNettyWebSocketSession;
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy;
import org.springframework.web.server.ServerWebExchange;

/**
 * A {@link RequestUpgradeStrategy} for use with Reactor Netty.
 *
 *
 * @since 5.0
 */
public class ReactorNettyRequestUpgradeStrategy implements RequestUpgradeStrategy {

	private int maxFramePayloadLength = NettyWebSocketSessionSupport.DEFAULT_FRAME_MAX_SIZE;


	/**
	 * Configure the maximum allowable frame payload length. Setting this value
	 * to your application's requirement may reduce denial of service attacks
	 * using long data frames.
	 * Corresponds to the argument with the same name in the constructor of
	 * {@link io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory
	 * WebSocketServerHandshakerFactory} in Netty.
	 * By default set to 65536 (64K).
	 * @param maxFramePayloadLength the max length for frames.
	 * @since 5.1
	 */
	public void setMaxFramePayloadLength(Integer maxFramePayloadLength) {
		this.maxFramePayloadLength = maxFramePayloadLength;
	}

	/**
	 * Return the configured max length for frames.
	 * @since 5.1
	 */
	public int getMaxFramePayloadLength() {
		return this.maxFramePayloadLength;
	}


	@Override
	public Mono<Void> upgrade(ServerWebExchange exchange, WebSocketHandler handler,
			@Nullable String subProtocol, Supplier<HandshakeInfo> handshakeInfoFactory) {

		ServerHttpResponse response = exchange.getResponse();
		HttpServerResponse reactorResponse = ((AbstractServerHttpResponse) response).getNativeResponse();
		HandshakeInfo handshakeInfo = handshakeInfoFactory.get();
		NettyDataBufferFactory bufferFactory = (NettyDataBufferFactory) response.bufferFactory();

		return reactorResponse.sendWebsocket(subProtocol, this.maxFramePayloadLength,
				(in, out) -> {
					ReactorNettyWebSocketSession session =
							new ReactorNettyWebSocketSession(
									in, out, handshakeInfo, bufferFactory, this.maxFramePayloadLength);
					return handler.handle(session);
				});
	}

}
