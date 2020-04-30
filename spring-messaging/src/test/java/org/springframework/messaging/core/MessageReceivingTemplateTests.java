

package org.springframework.messaging.core;

import java.io.Writer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.GenericMessageConverter;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.support.GenericMessage;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Unit tests for receiving operations in {@link AbstractMessagingTemplate}.
 *
 *
 * @see MessageRequestReplyTemplateTests
 */
public class MessageReceivingTemplateTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	private TestMessagingTemplate template;


	@Before
	public void setup() {
		this.template = new TestMessagingTemplate();
	}

	@Test
	public void receive() {
		Message<?> expected = new GenericMessage<>("payload");
		this.template.setDefaultDestination("home");
		this.template.setReceiveMessage(expected);
		Message<?> actual = this.template.receive();

		assertEquals("home", this.template.destination);
		assertSame(expected, actual);
	}

	@Test(expected = IllegalStateException.class)
	public void receiveMissingDefaultDestination() {
		this.template.receive();
	}

	@Test
	public void receiveFromDestination() {
		Message<?> expected = new GenericMessage<>("payload");
		this.template.setReceiveMessage(expected);
		Message<?> actual = this.template.receive("somewhere");

		assertEquals("somewhere", this.template.destination);
		assertSame(expected, actual);
	}

	@Test
	public void receiveAndConvert() {
		Message<?> expected = new GenericMessage<>("payload");
		this.template.setDefaultDestination("home");
		this.template.setReceiveMessage(expected);
		String payload = this.template.receiveAndConvert(String.class);

		assertEquals("home", this.template.destination);
		assertSame("payload", payload);
	}

	@Test
	public void receiveAndConvertFromDestination() {
		Message<?> expected = new GenericMessage<>("payload");
		this.template.setReceiveMessage(expected);
		String payload = this.template.receiveAndConvert("somewhere", String.class);

		assertEquals("somewhere", this.template.destination);
		assertSame("payload", payload);
	}

	@Test
	public void receiveAndConvertFailed() {
		Message<?> expected = new GenericMessage<>("not a number test");
		this.template.setReceiveMessage(expected);
		this.template.setMessageConverter(new GenericMessageConverter());

		thrown.expect(MessageConversionException.class);
		thrown.expectCause(isA(ConversionFailedException.class));
		this.template.receiveAndConvert("somewhere", Integer.class);
	}

	@Test
	public void receiveAndConvertNoConverter() {
		Message<?> expected = new GenericMessage<>("payload");
		this.template.setDefaultDestination("home");
		this.template.setReceiveMessage(expected);
		this.template.setMessageConverter(new GenericMessageConverter());
		try {
			this.template.receiveAndConvert(Writer.class);
		}
		catch (MessageConversionException ex) {
			assertTrue("Invalid exception message '" + ex.getMessage() + "'", ex.getMessage().contains("payload"));
			assertSame(expected, ex.getFailedMessage());
		}
	}



	private static class TestMessagingTemplate extends AbstractMessagingTemplate<String> {

		private String destination;

		private Message<?> receiveMessage;

		private void setReceiveMessage(Message<?> receiveMessage) {
			this.receiveMessage = receiveMessage;
		}

		@Override
		protected void doSend(String destination, Message<?> message) {
		}

		@Override
		protected Message<?> doReceive(String destination) {
			this.destination = destination;
			return this.receiveMessage;
		}

		@Override
		protected Message<?> doSendAndReceive(String destination, Message<?> requestMessage) {
			this.destination = destination;
			return null;
		}
	}

}
