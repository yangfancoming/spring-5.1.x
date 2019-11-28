

package org.springframework.core.codec;

import java.util.Map;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;

/**
 * Abstract base class for {@code Decoder} implementations that can decode
 * a {@code DataBuffer} directly to the target element type.
 *
 * <p>Sub-classes must implement {@link #decodeDataBuffer} to provide a way to
 * transform a {@code DataBuffer} to the target data type. The default
 * {@link #decode} implementation transforms each individual data buffer while
 * {@link #decodeToMono} applies "reduce" and transforms the aggregated buffer.
 *
 * <p>Sub-classes can override {@link #decode} in order to split the input stream
 * along different boundaries (e.g. on new line characters for {@code String})
 * or always reduce to a single data buffer (e.g. {@code Resource}).
 *
 * @since 5.0
 * @param <T> the element type
 */
public abstract class AbstractDataBufferDecoder<T> extends AbstractDecoder<T> {

	protected AbstractDataBufferDecoder(MimeType... supportedMimeTypes) {
		super(supportedMimeTypes);
	}

	@Override
	public Flux<T> decode(Publisher<DataBuffer> input, ResolvableType elementType,@Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
		return Flux.from(input).map(buffer -> decodeDataBuffer(buffer, elementType, mimeType, hints));
	}

	@Override
	public Mono<T> decodeToMono(Publisher<DataBuffer> input, ResolvableType elementType,@Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
		return DataBufferUtils.join(input).map(buffer -> decodeDataBuffer(buffer, elementType, mimeType, hints));
	}

	/**
	 * How to decode a {@code DataBuffer} to the target element type.
	 */
	protected abstract T decodeDataBuffer(DataBuffer buffer, ResolvableType elementType,@Nullable MimeType mimeType, @Nullable Map<String, Object> hints);


}
