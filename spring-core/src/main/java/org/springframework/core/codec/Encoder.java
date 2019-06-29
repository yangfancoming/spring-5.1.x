

package org.springframework.core.codec;

import java.util.List;
import java.util.Map;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;

/**
 * Strategy to encode a stream of Objects of type {@code <T>} into an output
 * stream of bytes.
 *
 * @author Sebastien Deleuze
 * @author Rossen Stoyanchev
 * @since 5.0
 * @param <T> the type of elements in the input stream
 */
public interface Encoder<T> {

	/**
	 * Whether the encoder supports the given source element type and the MIME
	 * type for the output stream.
	 * @param elementType the type of elements in the source stream
	 * @param mimeType the MIME type for the output stream
	 * (can be {@code null} if not specified)
	 * @return {@code true} if supported, {@code false} otherwise
	 */
	boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType);

	/**
	 * Encode a stream of Objects of type {@code T} into a {@link DataBuffer}
	 * output stream.
	 * @param inputStream the input stream of Objects to encode. If the input should be
	 * encoded as a single value rather than as a stream of elements, an instance of
	 * {@link Mono} should be used.
	 * @param bufferFactory for creating output stream {@code DataBuffer}'s
	 * @param elementType the expected type of elements in the input stream;
	 * this type must have been previously passed to the {@link #canEncode}
	 * method and it must have returned {@code true}.
	 * @param mimeType the MIME type for the output stream (optional)
	 * @param hints additional information about how to do encode
	 * @return the output stream
	 */
	Flux<DataBuffer> encode(Publisher<? extends T> inputStream, DataBufferFactory bufferFactory,
			ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints);

	/**
	 * Return the list of mime types this encoder supports.
	 */
	List<MimeType> getEncodableMimeTypes();

}
