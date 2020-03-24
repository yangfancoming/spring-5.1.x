

package org.springframework.messaging.converter;

import java.util.Locale;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;


public class GenericMessageConverterTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	private final ConversionService conversionService = new DefaultConversionService();
	private final GenericMessageConverter converter = new GenericMessageConverter(conversionService);

	@Test
	public void fromMessageWithConversion() {
		Message<String> content = MessageBuilder.withPayload("33").build();
		assertEquals(33, converter.fromMessage(content, Integer.class));
	}

	@Test
	public void fromMessageNoConverter() {
		Message<Integer> content = MessageBuilder.withPayload(1234).build();
		assertNull("No converter from integer to locale", converter.fromMessage(content, Locale.class));
	}

	@Test
	public void fromMessageWithFailedConversion() {
		Message<String> content = MessageBuilder.withPayload("test not a number").build();
		thrown.expect(MessageConversionException.class);
		thrown.expectCause(isA(ConversionException.class));
		converter.fromMessage(content, Integer.class);
	}
}
