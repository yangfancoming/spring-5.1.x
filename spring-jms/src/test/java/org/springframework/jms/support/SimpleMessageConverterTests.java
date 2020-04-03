

package org.springframework.jms.support;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.SimpleMessageConverter;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

/**
 * Unit tests for the {@link SimpleMessageConverter} class.
 *

 * @author Rick Evans
 * @since 18.09.2004
 */
public class SimpleMessageConverterTests {

	@Test
	public void testStringConversion() throws JMSException {
		Session session = mock(Session.class);
		TextMessage message = mock(TextMessage.class);

		String content = "test";

		given(session.createTextMessage(content)).willReturn(message);
		given(message.getText()).willReturn(content);

		SimpleMessageConverter converter = new SimpleMessageConverter();
		Message msg = converter.toMessage(content, session);
		assertEquals(content, converter.fromMessage(msg));
	}

	@Test
	public void testByteArrayConversion() throws JMSException {
		Session session = mock(Session.class);
		BytesMessage message = mock(BytesMessage.class);

		byte[] content = "test".getBytes();
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content);

		given(session.createBytesMessage()).willReturn(message);
		given(message.getBodyLength()).willReturn((long) content.length);
		given(message.readBytes(any(byte[].class))).willAnswer(new Answer<Integer>() {
			@Override
			public Integer answer(InvocationOnMock invocation) throws Throwable {
				return byteArrayInputStream.read((byte[]) invocation.getArguments()[0]);
			}
		});

		SimpleMessageConverter converter = new SimpleMessageConverter();
		Message msg = converter.toMessage(content, session);
		assertEquals(content.length, ((byte[]) converter.fromMessage(msg)).length);

		verify(message).writeBytes(content);
	}

	@Test
	public void testMapConversion() throws JMSException {

		Session session = mock(Session.class);
		MapMessage message = mock(MapMessage.class);

		Map<String, String> content = new HashMap<>(2);
		content.put("key1", "value1");
		content.put("key2", "value2");

		given(session.createMapMessage()).willReturn(message);
		given(message.getMapNames()).willReturn(Collections.enumeration(content.keySet()));
		given(message.getObject("key1")).willReturn("value1");
		given(message.getObject("key2")).willReturn("value2");

		SimpleMessageConverter converter = new SimpleMessageConverter();
		Message msg = converter.toMessage(content, session);
		assertEquals(content, converter.fromMessage(msg));

		verify(message).setObject("key1", "value1");
		verify(message).setObject("key2", "value2");
	}

	@Test
	public void testSerializableConversion() throws JMSException {
		Session session = mock(Session.class);
		ObjectMessage message = mock(ObjectMessage.class);

		Integer content = new Integer(5);

		given(session.createObjectMessage(content)).willReturn(message);
		given(message.getObject()).willReturn(content);

		SimpleMessageConverter converter = new SimpleMessageConverter();
		Message msg = converter.toMessage(content, session);
		assertEquals(content, converter.fromMessage(msg));
	}

	@Test(expected = MessageConversionException.class)
	public void testToMessageThrowsExceptionIfGivenNullObjectToConvert() throws Exception {
		new SimpleMessageConverter().toMessage(null, null);
	}

	@Test(expected = MessageConversionException.class)
	public void testToMessageThrowsExceptionIfGivenIncompatibleObjectToConvert() throws Exception {
		new SimpleMessageConverter().toMessage(new Object(), null);
	}

	@Test
	public void testToMessageSimplyReturnsMessageAsIsIfSuppliedWithMessage() throws JMSException {
		Session session = mock(Session.class);
		ObjectMessage message = mock(ObjectMessage.class);

		SimpleMessageConverter converter = new SimpleMessageConverter();
		Message msg = converter.toMessage(message, session);
		assertSame(message, msg);
	}

	@Test
	public void testFromMessageSimplyReturnsMessageAsIsIfSuppliedWithMessage() throws JMSException {
		Message message = mock(Message.class);

		SimpleMessageConverter converter = new SimpleMessageConverter();
		Object msg = converter.fromMessage(message);
		assertSame(message, msg);
	}

	@Test
	public void testMapConversionWhereMapHasNonStringTypesForKeys() throws JMSException {
		MapMessage message = mock(MapMessage.class);
		Session session = mock(Session.class);
		given(session.createMapMessage()).willReturn(message);

		Map<Integer, String> content = new HashMap<>(1);
		content.put(1, "value1");

		SimpleMessageConverter converter = new SimpleMessageConverter();
		try {
			converter.toMessage(content, session);
			fail("expected MessageConversionException");
		}
		catch (MessageConversionException ex) { /* expected */ }
	}

	@Test
	public void testMapConversionWhereMapHasNNullForKey() throws JMSException {
		MapMessage message = mock(MapMessage.class);
		Session session = mock(Session.class);
		given(session.createMapMessage()).willReturn(message);

		Map<Object, String> content = new HashMap<>(1);
		content.put(null, "value1");

		SimpleMessageConverter converter = new SimpleMessageConverter();
		try {
			converter.toMessage(content, session);
			fail("expected MessageConversionException");
		}
		catch (MessageConversionException ex) { /* expected */ }
	}

}
