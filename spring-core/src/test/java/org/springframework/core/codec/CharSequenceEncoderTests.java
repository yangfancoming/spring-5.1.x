

package org.springframework.core.codec;

import java.nio.charset.Charset;
import java.util.stream.Stream;

import org.junit.Test;
import reactor.core.publisher.Flux;

import org.springframework.core.ResolvableType;
import org.springframework.util.MimeTypeUtils;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.*;

/**
 * @author Sebastien Deleuze
 */
public class CharSequenceEncoderTests
		extends AbstractEncoderTestCase<CharSequenceEncoder> {

	private final String foo = "foo";

	private final String bar = "bar";

	public CharSequenceEncoderTests() {
		super(CharSequenceEncoder.textPlainOnly());
	}


	@Override
	public void canEncode() throws Exception {
		assertTrue(this.encoder.canEncode(ResolvableType.forClass(String.class),
				MimeTypeUtils.TEXT_PLAIN));
		assertTrue(this.encoder.canEncode(ResolvableType.forClass(StringBuilder.class),
				MimeTypeUtils.TEXT_PLAIN));
		assertTrue(this.encoder.canEncode(ResolvableType.forClass(StringBuffer.class),
				MimeTypeUtils.TEXT_PLAIN));
		assertFalse(this.encoder.canEncode(ResolvableType.forClass(Integer.class),
				MimeTypeUtils.TEXT_PLAIN));
		assertFalse(this.encoder.canEncode(ResolvableType.forClass(String.class),
				MimeTypeUtils.APPLICATION_JSON));

		// SPR-15464
		assertFalse(this.encoder.canEncode(ResolvableType.NONE, null));
	}

	@Override
	public void encode() {
		Flux<CharSequence> input = Flux.just(this.foo, this.bar);

		testEncodeAll(input, CharSequence.class, step -> step
				.consumeNextWith(expectString(this.foo))
				.consumeNextWith(expectString(this.bar))
				.verifyComplete());
	}

	@Test
	public void calculateCapacity() {
		String sequence = "Hello World!";
		Stream.of(UTF_8, UTF_16, ISO_8859_1, US_ASCII, Charset.forName("BIG5"))
				.forEach(charset -> {
					int capacity = this.encoder.calculateCapacity(sequence, charset);
					int length = sequence.length();
					assertTrue(String.format("%s has capacity %d; length %d", charset, capacity, length),
							capacity >= length);
				});

	}

}
