

package org.springframework.core.codec;

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
 * Simple pass-through encoder for {@link DataBuffer DataBuffers}.
 *
 * @author Arjen Poutsma
 * @since 5.0
 */
public class DataBufferEncoder extends AbstractEncoder<DataBuffer> {

	public DataBufferEncoder() {
		super(MimeTypeUtils.ALL);
	}


	@Override
	public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
		Class<?> clazz = elementType.toClass();
		return super.canEncode(elementType, mimeType) && DataBuffer.class.isAssignableFrom(clazz);
	}

	@Override
	public Flux<DataBuffer> encode(Publisher<? extends DataBuffer> inputStream,
			DataBufferFactory bufferFactory, ResolvableType elementType, @Nullable MimeType mimeType,
			@Nullable Map<String, Object> hints) {

		Flux<DataBuffer> flux = Flux.from(inputStream);

		if (logger.isDebugEnabled() && !Hints.isLoggingSuppressed(hints)) {
			flux = flux.doOnNext(buffer -> {
				String logPrefix = Hints.getLogPrefix(hints);
				logger.debug(logPrefix + "Writing " + buffer.readableByteCount() + " bytes");
			});
		}

		return flux;
	}

}
