

package org.springframework.core.codec;

import java.util.List;
import java.util.Map;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;

/**
 * Strategy for decoding a {@link DataBuffer} input stream into an output stream
 * of elements of type {@code <T>}.
 *
 * @author Sebastien Deleuze
 * @author Rossen Stoyanchev
 * @since 5.0
 * @param <T> the type of elements in the output stream
 */
public interface Decoder<T> {

	/**
	 * Whether the decoder supports the given target element type and the MIME
	 * type of the source stream.
	 * @param elementType the target element type for the output stream
	 * @param mimeType the mime type associated with the stream to decode
	 * (can be {@code null} if not specified)
	 * @return {@code true} if supported, {@code false} otherwise
	 */
	boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType);

	/**
	 * Decode a {@link DataBuffer} input stream into a Flux of {@code T}.
	 * @param inputStream the {@code DataBuffer} input stream to decode
	 * @param elementType the expected type of elements in the output stream;
	 * this type must have been previously passed to the {@link #canDecode}
	 * method and it must have returned {@code true}.
	 * @param mimeType the MIME type associated with the input stream (optional)
	 * @param hints additional information about how to do encode
	 * @return the output stream with decoded elements
	 */
	Flux<T> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType,
			@Nullable MimeType mimeType, @Nullable Map<String, Object> hints);

	/**
	 * Decode a {@link DataBuffer} input stream into a Mono of {@code T}.
	 * @param inputStream the {@code DataBuffer} input stream to decode
	 * @param elementType the expected type of elements in the output stream;
	 * this type must have been previously passed to the {@link #canDecode}
	 * method and it must have returned {@code true}.
	 * @param mimeType the MIME type associated with the input stream (optional)
	 * @param hints additional information about how to do encode
	 * @return the output stream with the decoded element
	 */
	Mono<T> decodeToMono(Publisher<DataBuffer> inputStream, ResolvableType elementType,
			@Nullable MimeType mimeType, @Nullable Map<String, Object> hints);

	/**
	 * Return the list of MIME types this decoder supports.
	 */
	List<MimeType> getDecodableMimeTypes();

}
