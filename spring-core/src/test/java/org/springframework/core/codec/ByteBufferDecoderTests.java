

package org.springframework.core.codec;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.junit.Test;
import reactor.core.publisher.Flux;

import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.util.MimeTypeUtils;

import static org.junit.Assert.*;


public class ByteBufferDecoderTests extends AbstractDecoderTestCase<ByteBufferDecoder> {

	private final byte[] fooBytes = "foo".getBytes(StandardCharsets.UTF_8);

	private final byte[] barBytes = "bar".getBytes(StandardCharsets.UTF_8);


	public ByteBufferDecoderTests() {
		super(new ByteBufferDecoder());
	}

	@Override
	@Test
	public void canDecode() {
		assertTrue(this.decoder.canDecode(ResolvableType.forClass(ByteBuffer.class),MimeTypeUtils.TEXT_PLAIN));
		assertFalse(this.decoder.canDecode(ResolvableType.forClass(Integer.class),MimeTypeUtils.TEXT_PLAIN));
		assertTrue(this.decoder.canDecode(ResolvableType.forClass(ByteBuffer.class),MimeTypeUtils.APPLICATION_JSON));
	}

	@Override
	@Test
	public void decode() {
		Flux<DataBuffer> input = Flux.concat(
				dataBuffer(this.fooBytes),
				dataBuffer(this.barBytes));

		testDecodeAll(input, ByteBuffer.class, step -> step
				.consumeNextWith(expectByteBuffer(ByteBuffer.wrap(this.fooBytes)))
				.consumeNextWith(expectByteBuffer(ByteBuffer.wrap(this.barBytes)))
				.verifyComplete());


	}

	@Override
	@Test
	public void decodeToMono() {
		Flux<DataBuffer> input = Flux.concat(
				dataBuffer(this.fooBytes),
				dataBuffer(this.barBytes));
		ByteBuffer expected = ByteBuffer.allocate(this.fooBytes.length + this.barBytes.length);
		expected.put(this.fooBytes).put(this.barBytes).flip();

		testDecodeToMonoAll(input, ByteBuffer.class, step -> step
				.consumeNextWith(expectByteBuffer(expected))
				.verifyComplete());

	}

	private Consumer<ByteBuffer> expectByteBuffer(ByteBuffer expected) {
		return actual -> assertEquals(expected, actual);
	}

}
