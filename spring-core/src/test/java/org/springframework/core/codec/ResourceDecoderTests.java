

package org.springframework.core.codec;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;
import reactor.core.publisher.Flux;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StreamUtils;

import static org.junit.Assert.*;
import static org.springframework.core.ResolvableType.*;


public class ResourceDecoderTests extends AbstractDecoderTestCase<ResourceDecoder> {

	private final byte[] fooBytes = "foo".getBytes(StandardCharsets.UTF_8);
	private final byte[] barBytes = "bar".getBytes(StandardCharsets.UTF_8);

	public ResourceDecoderTests() {
		super(new ResourceDecoder());
	}

	@Override
	@Test
	public void canDecode() {
		assertTrue(this.decoder.canDecode(forClass(InputStreamResource.class), MimeTypeUtils.TEXT_PLAIN));
		assertTrue(this.decoder.canDecode(forClass(ByteArrayResource.class), MimeTypeUtils.TEXT_PLAIN));
		assertTrue(this.decoder.canDecode(forClass(Resource.class), MimeTypeUtils.TEXT_PLAIN));
		assertTrue(this.decoder.canDecode(forClass(InputStreamResource.class), MimeTypeUtils.APPLICATION_JSON));
		assertFalse(this.decoder.canDecode(forClass(Object.class), MimeTypeUtils.APPLICATION_JSON));
	}

	@Override
	@Test
	public void decode() {
		Flux<DataBuffer> input = Flux.concat(dataBuffer(this.fooBytes), dataBuffer(this.barBytes));
		testDecodeAll(input, Resource.class, step -> step
				.consumeNextWith(resource -> {
					try {
						byte[] bytes = StreamUtils.copyToByteArray(resource.getInputStream());
						assertEquals("foobar", new String(bytes));
					}catch (IOException e) {
						fail(e.getMessage());
					}
				})
				.expectComplete()
				.verify());
	}

	@Override
	public void decodeToMono() {
		Flux<DataBuffer> input = Flux.concat(dataBuffer(this.fooBytes),dataBuffer(this.barBytes));

		testDecodeToMonoAll(input, Resource.class, step -> step
				.consumeNextWith(resource -> {
					try {
						byte[] bytes = StreamUtils.copyToByteArray(resource.getInputStream());
						assertEquals("foobar", new String(bytes));
					}catch (IOException e) {
						fail(e.getMessage());
					}
				})
				.expectComplete()
				.verify());
	}

}
