
package org.springframework.messaging.simp.stomp;

import java.nio.ByteBuffer;
import java.util.List;

import org.springframework.messaging.Message;
import org.springframework.messaging.tcp.reactor.AbstractNioBufferReactorNettyCodec;

/**
 * Simple delegation to StompDecoder and StompEncoder.
 *
 *
 * @since 5.0
 */
public class StompReactorNettyCodec extends AbstractNioBufferReactorNettyCodec<byte[]> {

	private final StompDecoder decoder;

	private final StompEncoder encoder;


	public StompReactorNettyCodec() {
		this(new StompDecoder());
	}

	public StompReactorNettyCodec(StompDecoder decoder) {
		this(decoder, new StompEncoder());
	}

	public StompReactorNettyCodec(StompDecoder decoder, StompEncoder encoder) {
		this.decoder = decoder;
		this.encoder = encoder;
	}


	@Override
	protected List<Message<byte[]>> decodeInternal(ByteBuffer nioBuffer) {
		return this.decoder.decode(nioBuffer);
	}

	protected ByteBuffer encodeInternal(Message<byte[]> message) {
		return ByteBuffer.wrap(this.encoder.encode(message));
	}

}
