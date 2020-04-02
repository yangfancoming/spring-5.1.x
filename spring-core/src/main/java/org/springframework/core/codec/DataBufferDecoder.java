

package org.springframework.core.codec;

import java.util.Map;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

/**
 * Simple pass-through decoder for {@link DataBuffer DataBuffers}.
 *
 * <strong>Note:</strong> The data buffers should be released via
 * {@link org.springframework.core.io.buffer.DataBufferUtils#release(DataBuffer)}
 * after they have been consumed. In addition, if using {@code Flux} or
 * {@code Mono} operators such as flatMap, reduce, and others that prefetch,
 * cache, and skip or filter out data items internally, please add
 * {@code doOnDiscard(PooledDataBuffer.class, DataBufferUtils::release)} to the
 * composition chain to ensure cached data buffers are released prior to an
 * error or cancellation signal.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 5.0
 */
public class DataBufferDecoder extends AbstractDataBufferDecoder<DataBuffer> {

	public DataBufferDecoder() {
		super(MimeTypeUtils.ALL);
	}


	@Override
	public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
		return (DataBuffer.class.isAssignableFrom(elementType.toClass()) &&
				super.canDecode(elementType, mimeType));
	}

	@Override
	public Flux<DataBuffer> decode(Publisher<DataBuffer> input, ResolvableType elementType,
			@Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {

		return Flux.from(input);
	}

	@Override
	protected DataBuffer decodeDataBuffer(DataBuffer buffer, ResolvableType elementType,
			@Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {

		if (logger.isDebugEnabled()) {
			logger.debug(Hints.getLogPrefix(hints) + "Read " + buffer.readableByteCount() + " bytes");
		}
		return buffer;
	}

}
