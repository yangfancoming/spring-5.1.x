

package org.springframework.messaging.converter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import static org.junit.Assert.*;

/**
 * Unit tests for
 * {@link org.springframework.messaging.converter.AbstractMessageConverter}.
 *
 *
 */
public class MessageConverterTests {

	private TestMessageConverter converter = new TestMessageConverter();


	@Test
	public void supportsTargetClass() {
		Message<String> message = MessageBuilder.withPayload("ABC").build();

		assertEquals("success-from", this.converter.fromMessage(message, String.class));
		assertNull(this.converter.fromMessage(message, Integer.class));
	}

	@Test
	public void supportsMimeType() {
		Message<String> message = MessageBuilder.withPayload(
				"ABC").setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.TEXT_PLAIN).build();

		assertEquals("success-from", this.converter.fromMessage(message, String.class));
	}

	@Test
	public void supportsMimeTypeNotSupported() {
		Message<String> message = MessageBuilder.withPayload(
				"ABC").setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON).build();

		assertNull(this.converter.fromMessage(message, String.class));
	}

	@Test
	public void supportsMimeTypeNotSpecified() {
		Message<String> message = MessageBuilder.withPayload("ABC").build();
		assertEquals("success-from", this.converter.fromMessage(message, String.class));
	}

	@Test
	public void supportsMimeTypeNoneConfigured() {
		Message<String> message = MessageBuilder.withPayload(
				"ABC").setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON).build();
		this.converter = new TestMessageConverter(Collections.<MimeType>emptyList());

		assertEquals("success-from", this.converter.fromMessage(message, String.class));
	}

	@Test
	public void canConvertFromStrictContentTypeMatch() {
		this.converter = new TestMessageConverter(Arrays.asList(MimeTypeUtils.TEXT_PLAIN));
		this.converter.setStrictContentTypeMatch(true);

		Message<String> message = MessageBuilder.withPayload("ABC").build();
		assertFalse(this.converter.canConvertFrom(message, String.class));

		message = MessageBuilder.withPayload("ABC")
				.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.TEXT_PLAIN).build();
		assertTrue(this.converter.canConvertFrom(message, String.class));

	}

	@Test(expected = IllegalArgumentException.class)
	public void setStrictContentTypeMatchWithNoSupportedMimeTypes() {
		this.converter = new TestMessageConverter(Collections.<MimeType>emptyList());
		this.converter.setStrictContentTypeMatch(true);
	}

	@Test
	public void toMessageWithHeaders() {
		Map<String, Object> map = new HashMap<>();
		map.put("foo", "bar");
		MessageHeaders headers = new MessageHeaders(map);
		Message<?> message = this.converter.toMessage("ABC", headers);

		assertNotNull(message.getHeaders().getId());
		assertNotNull(message.getHeaders().getTimestamp());
		assertEquals(MimeTypeUtils.TEXT_PLAIN, message.getHeaders().get(MessageHeaders.CONTENT_TYPE));
		assertEquals("bar", message.getHeaders().get("foo"));
	}

	@Test
	public void toMessageWithMutableMessageHeaders() {
		SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
		accessor.setHeader("foo", "bar");
		accessor.setNativeHeader("fooNative", "barNative");
		accessor.setLeaveMutable(true);

		MessageHeaders headers = accessor.getMessageHeaders();
		Message<?> message = this.converter.toMessage("ABC", headers);

		assertSame(headers, message.getHeaders());
		assertNull(message.getHeaders().getId());
		assertNull(message.getHeaders().getTimestamp());
		assertEquals(MimeTypeUtils.TEXT_PLAIN, message.getHeaders().get(MessageHeaders.CONTENT_TYPE));
	}

	@Test
	public void toMessageContentTypeHeader() {
		Message<?> message = this.converter.toMessage("ABC", null);
		assertEquals(MimeTypeUtils.TEXT_PLAIN, message.getHeaders().get(MessageHeaders.CONTENT_TYPE));
	}


	private static class TestMessageConverter extends AbstractMessageConverter {

		public TestMessageConverter() {
			super(MimeTypeUtils.TEXT_PLAIN);
		}

		public TestMessageConverter(Collection<MimeType> supportedMimeTypes) {
			super(supportedMimeTypes);
		}

		@Override
		protected boolean supports(Class<?> clazz) {
			return String.class.equals(clazz);
		}

		@Override
		protected Object convertFromInternal(Message<?> message, Class<?> targetClass,
				@Nullable Object conversionHint) {

			return "success-from";
		}

		@Override
		protected Object convertToInternal(Object payload, @Nullable MessageHeaders headers,
				@Nullable Object conversionHint) {

			return "success-to";
		}
	}

}
