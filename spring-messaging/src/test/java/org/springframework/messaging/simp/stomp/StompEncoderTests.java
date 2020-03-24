

package org.springframework.messaging.simp.stomp;

import org.junit.Test;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test fixture for {@link StompEncoder}.
 *
 * @author Andy Wilkinson
 * @author Stephane Maldini
 */
public class StompEncoderTests {

	private final StompEncoder encoder = new StompEncoder();


	@Test
	public void encodeFrameWithNoHeadersAndNoBody() {
		StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.DISCONNECT);
		Message<byte[]> frame = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());

		assertEquals("DISCONNECT\n\n\0", new String(encoder.encode(frame)));
	}

	@Test
	public void encodeFrameWithHeaders() {
		StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.CONNECT);
		headers.setAcceptVersion("1.2");
		headers.setHost("github.org");
		Message<byte[]> frame = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());
		String frameString = new String(encoder.encode(frame));

		assertTrue(
				"CONNECT\naccept-version:1.2\nhost:github.org\n\n\0".equals(frameString) ||
				"CONNECT\nhost:github.org\naccept-version:1.2\n\n\0".equals(frameString));
	}

	@Test
	public void encodeFrameWithHeadersThatShouldBeEscaped() {
		StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.DISCONNECT);
		headers.addNativeHeader("a:\r\n\\b",  "alpha:bravo\r\n\\");
		Message<byte[]> frame = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());

		assertEquals("DISCONNECT\na\\c\\r\\n\\\\b:alpha\\cbravo\\r\\n\\\\\n\n\0",
				new String(encoder.encode(frame)));
	}

	@Test
	public void encodeFrameWithHeadersBody() {
		StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SEND);
		headers.addNativeHeader("a", "alpha");
		Message<byte[]> frame = MessageBuilder.createMessage(
				"Message body".getBytes(), headers.getMessageHeaders());

		assertEquals("SEND\na:alpha\ncontent-length:12\n\nMessage body\0",
				new String(encoder.encode(frame)));
	}

	@Test
	public void encodeFrameWithContentLengthPresent() {
		StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SEND);
		headers.setContentLength(12);
		Message<byte[]> frame = MessageBuilder.createMessage(
				"Message body".getBytes(), headers.getMessageHeaders());

		assertEquals("SEND\ncontent-length:12\n\nMessage body\0",
				new String(encoder.encode(frame)));
	}

}
