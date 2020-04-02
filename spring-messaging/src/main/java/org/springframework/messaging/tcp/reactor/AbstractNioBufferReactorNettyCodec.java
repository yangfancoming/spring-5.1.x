

package org.springframework.messaging.tcp.reactor;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;

import io.netty.buffer.ByteBuf;

import org.springframework.messaging.Message;

/**
 * Convenient base class for {@link ReactorNettyCodec} implementations that need
 * to work with NIO {@link ByteBuffer ByteBuffers}.
 *
 * @author Rossen Stoyanchev
 * @since 5.0
 * @param  the message payload type
 */
public abstract class AbstractNioBufferReactorNettyCodec implements ReactorNettyCodec {

	@Override
	public Collection<Message> decode(ByteBuf inputBuffer) {
		ByteBuffer nioBuffer = inputBuffer.nioBuffer();
		int start = nioBuffer.position();
		List<Message> messages = decodeInternal(nioBuffer);
		inputBuffer.skipBytes(nioBuffer.position() - start);
		return messages;
	}

	@Override
	public void encode(Message message, ByteBuf outputBuffer) {
		outputBuffer.writeBytes(encodeInternal(message));
	}


	protected abstract List<Message> decodeInternal(ByteBuffer nioBuffer);

	protected abstract ByteBuffer encodeInternal(Message message);

}
