

package org.springframework.core.codec;

import java.nio.ByteBuffer;
import java.util.Map;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

/**
 * Encoder for {@link ByteBuffer ByteBuffers}.
 *
 * @author Sebastien Deleuze
 * @since 5.0
 */
public class ByteBufferEncoder extends AbstractEncoder<ByteBuffer> {

	public ByteBufferEncoder() {
		super(MimeTypeUtils.ALL);
	}


	@Override
	public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
		Class<?> clazz = elementType.toClass();
		return super.canEncode(elementType, mimeType) && ByteBuffer.class.isAssignableFrom(clazz);
	}

	@Override
	public Flux<DataBuffer> encode(Publisher<? extends ByteBuffer> inputStream,
			DataBufferFactory bufferFactory, ResolvableType elementType, @Nullable MimeType mimeType,
			@Nullable Map<String, Object> hints) {

		return Flux.from(inputStream).map(byteBuffer -> {
			DataBuffer dataBuffer = bufferFactory.wrap(byteBuffer);
			if (logger.isDebugEnabled() && !Hints.isLoggingSuppressed(hints)) {
				String logPrefix = Hints.getLogPrefix(hints);
				logger.debug(logPrefix + "Writing " + dataBuffer.readableByteCount() + " bytes");
			}
			return dataBuffer;
		});
	}

}
