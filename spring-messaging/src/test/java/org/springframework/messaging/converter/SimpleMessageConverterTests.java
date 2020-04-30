

package org.springframework.messaging.converter;

import java.util.Collections;

import org.junit.Test;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageHeaderAccessor;

import static org.junit.Assert.*;

/**
 * Unit tests for
 * {@link org.springframework.messaging.converter.SimpleMessageConverter}.
 *
 *
 */
public class SimpleMessageConverterTests {

	private final SimpleMessageConverter converter = new SimpleMessageConverter();


	@Test
	public void toMessageWithPayloadAndHeaders() {
		MessageHeaders headers = new MessageHeaders(Collections.<String, Object>singletonMap("foo", "bar"));
		Message<?> message = this.converter.toMessage("payload", headers);

		assertEquals("payload", message.getPayload());
		assertEquals("bar", message.getHeaders().get("foo"));
	}

	@Test
	public void toMessageWithPayloadAndMutableHeaders() {
		MessageHeaderAccessor accessor = new MessageHeaderAccessor();
		accessor.setHeader("foo", "bar");
		accessor.setLeaveMutable(true);
		MessageHeaders headers = accessor.getMessageHeaders();

		Message<?> message = this.converter.toMessage("payload", headers);

		assertEquals("payload", message.getPayload());
		assertSame(headers, message.getHeaders());
		assertEquals("bar", message.getHeaders().get("foo"));
	}
}
