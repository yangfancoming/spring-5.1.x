
package org.springframework.messaging.tcp.reactor;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;

import io.netty.buffer.ByteBuf;

import org.springframework.messaging.Message;

/**
 * Simple holder for a decoding {@link Function} and an encoding
 * {@link BiConsumer} to use with Reactor Netty.
 *
 * @author Rossen Stoyanchev
 * @since 5.0
 * @param  the message payload type
 */
public interface ReactorNettyCodec {

	/**
	 * Decode the input {@link ByteBuf} into one or more {@link Message Messages}.
	 * @param inputBuffer the input buffer to decode from
	 * @return 0 or more decoded messages
	 */
	Collection<Message> decode(ByteBuf inputBuffer);

	/**
	 * Encode the given {@link Message} to the output {@link ByteBuf}.
	 * @param message the message to encode
	 * @param outputBuffer the buffer to write to
	 */
	void encode(Message message, ByteBuf outputBuffer);

}
