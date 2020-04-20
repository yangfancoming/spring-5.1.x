

package org.springframework.core.codec;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.junit.Test;
import reactor.core.publisher.Flux;

import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.util.MimeTypeUtils;

import static org.junit.Assert.*;


public class ByteArrayDecoderTests extends AbstractDecoderTestCase<ByteArrayDecoder> {

	private final byte[] fooBytes = "foo".getBytes(StandardCharsets.UTF_8);

	private final byte[] barBytes = "bar".getBytes(StandardCharsets.UTF_8);


	public ByteArrayDecoderTests() {
		super(new ByteArrayDecoder());
	}

	@Override
	@Test
	public void canDecode() {
		assertTrue(this.decoder.canDecode(ResolvableType.forClass(byte[].class),MimeTypeUtils.TEXT_PLAIN));
		assertFalse(this.decoder.canDecode(ResolvableType.forClass(Integer.class),MimeTypeUtils.TEXT_PLAIN));
		assertTrue(this.decoder.canDecode(ResolvableType.forClass(byte[].class),MimeTypeUtils.APPLICATION_JSON));
	}

	@Override
	@Test
	public void decode() {
		Flux<DataBuffer> input = Flux.concat( dataBuffer(this.fooBytes),dataBuffer(this.barBytes));
		testDecodeAll(input, byte[].class, step -> step
				.consumeNextWith(expectBytes(this.fooBytes))
				.consumeNextWith(expectBytes(this.barBytes))
				.verifyComplete());

	}

	@Override
	@Test
	public void decodeToMono() {
		Flux<DataBuffer> input = Flux.concat(
				dataBuffer(this.fooBytes),
				dataBuffer(this.barBytes));
		byte[] expected = new byte[this.fooBytes.length + this.barBytes.length];
		System.arraycopy(this.fooBytes, 0, expected, 0, this.fooBytes.length);
		System.arraycopy(this.barBytes, 0, expected, this.fooBytes.length, this.barBytes.length);
		testDecodeToMonoAll(input, byte[].class, step -> step
				.consumeNextWith(expectBytes(expected))
				.verifyComplete());
	}

	private Consumer<byte[]> expectBytes(byte[] expected) {
		return bytes -> assertArrayEquals(expected, bytes);
	}

}
