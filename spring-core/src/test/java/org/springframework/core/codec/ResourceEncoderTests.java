

package org.springframework.core.codec;

import java.util.Map;

import org.junit.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import org.springframework.core.ResolvableType;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.*;


public class ResourceEncoderTests extends AbstractEncoderTestCase<ResourceEncoder> {

	private final byte[] bytes = "foo".getBytes(UTF_8);

	public ResourceEncoderTests() {
		super(new ResourceEncoder());
	}

	@Override
	@Test
	public void canEncode() {
		assertTrue(this.encoder.canEncode(ResolvableType.forClass(InputStreamResource.class),MimeTypeUtils.TEXT_PLAIN));
		assertTrue(this.encoder.canEncode(ResolvableType.forClass(ByteArrayResource.class),MimeTypeUtils.TEXT_PLAIN));
		assertTrue(this.encoder.canEncode(ResolvableType.forClass(Resource.class),MimeTypeUtils.TEXT_PLAIN));
		assertTrue(this.encoder.canEncode(ResolvableType.forClass(InputStreamResource.class),MimeTypeUtils.APPLICATION_JSON));

		// SPR-15464
		assertFalse(this.encoder.canEncode(ResolvableType.NONE, null));
	}

	@Override
	public void encode() {
		Flux<Resource> input = Flux.just(new ByteArrayResource(this.bytes));

		testEncodeAll(input, Resource.class, step -> step
				.consumeNextWith(expectBytes(this.bytes))
				.verifyComplete());
	}

	@Override
	protected void testEncodeError(Publisher<?> input, ResolvableType outputType,@Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
		Flux<Resource> i = Flux.error(new InputException());
		Flux<DataBuffer> result = ((Encoder<Resource>) this.encoder).encode(i,this.bufferFactory, outputType,mimeType, hints);
		StepVerifier.create(result).expectError(InputException.class).verify();
	}

}
