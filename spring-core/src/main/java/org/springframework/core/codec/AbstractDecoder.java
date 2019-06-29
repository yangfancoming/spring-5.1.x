

package org.springframework.core.codec;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;

/**
 * Abstract base class for {@link Decoder} implementations.
 *
 * @author Sebastien Deleuze
 * @author Arjen Poutsma
 * @since 5.0
 * @param <T> the element type
 */
public abstract class AbstractDecoder<T> implements Decoder<T> {

	private final List<MimeType> decodableMimeTypes;

	protected Log logger = LogFactory.getLog(getClass());


	protected AbstractDecoder(MimeType... supportedMimeTypes) {
		this.decodableMimeTypes = Arrays.asList(supportedMimeTypes);
	}


	/**
	 * Set an alternative logger to use than the one based on the class name.
	 * @param logger the logger to use
	 * @since 5.1
	 */
	public void setLogger(Log logger) {
		this.logger = logger;
	}

	/**
	 * Return the currently configured Logger.
	 * @since 5.1
	 */
	public Log getLogger() {
		return logger;
	}


	@Override
	public List<MimeType> getDecodableMimeTypes() {
		return this.decodableMimeTypes;
	}

	@Override
	public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
		if (mimeType == null) {
			return true;
		}
		for (MimeType candidate : this.decodableMimeTypes) {
			if (candidate.isCompatibleWith(mimeType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Mono<T> decodeToMono(Publisher<DataBuffer> inputStream, ResolvableType elementType,
			@Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {

		throw new UnsupportedOperationException();
	}

}
