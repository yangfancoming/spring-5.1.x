

package org.springframework.http.codec.protobuf;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Consumer;

import com.google.protobuf.Message;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.codec.AbstractEncoderTestCase;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.protobuf.Msg;
import org.springframework.protobuf.SecondMsg;
import org.springframework.util.MimeType;

import static org.junit.Assert.*;
import static org.springframework.core.ResolvableType.forClass;

/**
 * Unit tests for {@link ProtobufEncoder}.
 *
 * @author Sebastien Deleuze
 */
public class ProtobufEncoderTests extends AbstractEncoderTestCase<ProtobufEncoder> {

	private final static MimeType PROTOBUF_MIME_TYPE = new MimeType("application", "x-protobuf");

	private Msg msg1 =
			Msg.newBuilder().setFoo("Foo").setBlah(SecondMsg.newBuilder().setBlah(123).build()).build();

	private Msg msg2 =
			Msg.newBuilder().setFoo("Bar").setBlah(SecondMsg.newBuilder().setBlah(456).build()).build();


	public ProtobufEncoderTests() {
		super(new ProtobufEncoder());
	}

	@Override
	@Test
	public void canEncode() {
		assertTrue(this.encoder.canEncode(forClass(Msg.class), null));
		assertTrue(this.encoder.canEncode(forClass(Msg.class), PROTOBUF_MIME_TYPE));
		assertTrue(this.encoder.canEncode(forClass(Msg.class), MediaType.APPLICATION_OCTET_STREAM));
		assertFalse(this.encoder.canEncode(forClass(Msg.class), MediaType.APPLICATION_JSON));
		assertFalse(this.encoder.canEncode(forClass(Object.class), PROTOBUF_MIME_TYPE));
	}

	@Override
	@Test
	public void encode() {
		Mono<Message> input = Mono.just(this.msg1);

		testEncodeAll(input, Msg.class, step -> step
				.consumeNextWith(dataBuffer -> {
					try {
						assertEquals(this.msg1, Msg.parseFrom(dataBuffer.asInputStream()));

					}
					catch (IOException ex) {
						throw new UncheckedIOException(ex);
					}
					finally {
						DataBufferUtils.release(dataBuffer);
					}
				})
				.verifyComplete());
	}

	@Test
	public void encodeStream() {
		Flux<Message> input = Flux.just(this.msg1, this.msg2);

		testEncodeAll(input, Msg.class, step -> step
				.consumeNextWith(expect(this.msg1))
				.consumeNextWith(expect(this.msg2))
				.verifyComplete());
	}

	protected final Consumer<DataBuffer> expect(Msg msg) {
		return dataBuffer -> {
			try {
				assertEquals(msg, Msg.parseDelimitedFrom(dataBuffer.asInputStream()));

			}
			catch (IOException ex) {
				throw new UncheckedIOException(ex);
			}
			finally {
				DataBufferUtils.release(dataBuffer);
			}
		};
	}
}
