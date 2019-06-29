

package org.springframework.core.codec;

import java.nio.charset.StandardCharsets;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.util.MimeTypeUtils;

import static org.junit.Assert.*;

/**
 * @author Sebastien Deleuze
 */
public class DataBufferEncoderTests extends AbstractEncoderTestCase<DataBufferEncoder> {

	private final byte[] fooBytes = "foo".getBytes(StandardCharsets.UTF_8);

	private final byte[] barBytes = "bar".getBytes(StandardCharsets.UTF_8);

	public DataBufferEncoderTests() {
		super(new DataBufferEncoder());
	}


	@Override
	@Test
	public void canEncode() {
		assertTrue(this.encoder.canEncode(ResolvableType.forClass(DataBuffer.class),
				MimeTypeUtils.TEXT_PLAIN));
		assertFalse(this.encoder.canEncode(ResolvableType.forClass(Integer.class),
				MimeTypeUtils.TEXT_PLAIN));
		assertTrue(this.encoder.canEncode(ResolvableType.forClass(DataBuffer.class),
				MimeTypeUtils.APPLICATION_JSON));

		// SPR-15464
		assertFalse(this.encoder.canEncode(ResolvableType.NONE, null));
	}

	@Override
	public void encode() throws Exception {
		Flux<DataBuffer> input = Flux.just(this.fooBytes, this.barBytes)
				.flatMap(bytes -> Mono.defer(() -> {
					DataBuffer dataBuffer = this.bufferFactory.allocateBuffer(bytes.length);
					dataBuffer.write(bytes);
					return Mono.just(dataBuffer);
				}));

		testEncodeAll(input, DataBuffer.class, step -> step
				.consumeNextWith(expectBytes(this.fooBytes))
				.consumeNextWith(expectBytes(this.barBytes))
				.verifyComplete());

	}


}
