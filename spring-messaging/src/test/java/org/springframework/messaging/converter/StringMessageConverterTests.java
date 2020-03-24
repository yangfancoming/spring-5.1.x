

package org.springframework.messaging.converter;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import static org.junit.Assert.*;

/**
 * Test fixture for {@link org.springframework.messaging.converter.StringMessageConverter}.
 *
 * @author Rossen Stoyanchev
 */
public class StringMessageConverterTests {

	private final StringMessageConverter converter = new StringMessageConverter();


	@Test
	public void fromByteArrayMessage() {
		Message<byte[]> message = MessageBuilder.withPayload(
				"ABC".getBytes()).setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.TEXT_PLAIN).build();
		assertEquals("ABC", this.converter.fromMessage(message, String.class));
	}

	@Test
	public void fromStringMessage() {
		Message<String> message = MessageBuilder.withPayload(
				"ABC").setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.TEXT_PLAIN).build();
		assertEquals("ABC", this.converter.fromMessage(message, String.class));
	}

	@Test
	public void fromMessageNoContentTypeHeader() {
		Message<byte[]> message = MessageBuilder.withPayload("ABC".getBytes()).build();
		assertEquals("ABC", this.converter.fromMessage(message, String.class));
	}

	@Test
	public void fromMessageCharset() {
		String payload = "H\u00e9llo W\u00f6rld";
		Message<byte[]> message = MessageBuilder.withPayload(payload.getBytes(StandardCharsets.ISO_8859_1))
				.setHeader(MessageHeaders.CONTENT_TYPE, new MimeType("text", "plain", StandardCharsets.ISO_8859_1)).build();
		assertEquals(payload, this.converter.fromMessage(message, String.class));
	}

	@Test
	public void fromMessageDefaultCharset() {
		String payload = "H\u00e9llo W\u00f6rld";
		Message<byte[]> message = MessageBuilder.withPayload(payload.getBytes(StandardCharsets.UTF_8)).build();
		assertEquals(payload, this.converter.fromMessage(message, String.class));
	}

	@Test
	public void fromMessageTargetClassNotSupported() {
		Message<byte[]> message = MessageBuilder.withPayload("ABC".getBytes()).build();
		assertNull(this.converter.fromMessage(message, Integer.class));
	}

	@Test
	public void fromMessageByteArray() {
		Message<byte[]> message = MessageBuilder.withPayload(
				"ABC".getBytes()).setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.TEXT_PLAIN).build();
		assertEquals("ABC", this.converter.fromMessage(message, String.class));
	}

	@Test
	public void toMessage() {
		Map<String, Object> map = new HashMap<>();
		map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.TEXT_PLAIN);
		MessageHeaders headers = new MessageHeaders(map);
		Message<?> message = this.converter.toMessage("ABC", headers);

		assertEquals("ABC", new String(((byte[]) message.getPayload())));
	}

}
