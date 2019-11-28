

package org.springframework.core.codec;

import java.io.ByteArrayInputStream;
import java.util.Map;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import org.springframework.core.ResolvableType;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

/**
 * Decoder for {@link Resource Resources}.
 * @since 5.0
 */
public class ResourceDecoder extends AbstractDataBufferDecoder<Resource> {

	public ResourceDecoder() {
		super(MimeTypeUtils.ALL);
	}


	@Override
	public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
		return (Resource.class.isAssignableFrom(elementType.toClass()) && super.canDecode(elementType, mimeType));
	}

	@Override
	public Flux<Resource> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType,@Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
		return Flux.from(decodeToMono(inputStream, elementType, mimeType, hints));
	}

	@Override
	protected Resource decodeDataBuffer(DataBuffer dataBuffer, ResolvableType elementType,@Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
		byte[] bytes = new byte[dataBuffer.readableByteCount()];
		dataBuffer.read(bytes);
		DataBufferUtils.release(dataBuffer);

		if (logger.isDebugEnabled()) {
			logger.debug(Hints.getLogPrefix(hints) + "Read " + bytes.length + " bytes");
		}

		Class<?> clazz = elementType.toClass();
		if (clazz == InputStreamResource.class) {
			return new InputStreamResource(new ByteArrayInputStream(bytes));
		}
		else if (Resource.class.isAssignableFrom(clazz)) {
			return new ByteArrayResource(bytes);
		}
		else {
			throw new IllegalStateException("Unsupported resource class: " + clazz);
		}
	}

}
