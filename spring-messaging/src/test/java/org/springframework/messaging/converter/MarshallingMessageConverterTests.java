

package org.springframework.messaging.converter;

import static org.junit.Assert.*;
import static org.xmlunit.diff.ComparisonType.*;
import static org.xmlunit.diff.DifferenceEvaluators.*;
import static org.xmlunit.matchers.CompareMatcher.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.annotation.XmlRootElement;

import org.junit.Before;
import org.junit.Test;
import org.xmlunit.diff.DifferenceEvaluator;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;


public class MarshallingMessageConverterTests {

	private MarshallingMessageConverter converter;


	@Before
	public void createMarshaller() throws Exception {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(MyBean.class);
		marshaller.afterPropertiesSet();

		this.converter = new MarshallingMessageConverter(marshaller);
	}


	@Test
	public void fromMessage() throws Exception {
		String payload = "<myBean><name>Foo</name></myBean>";
		Message<?> message = MessageBuilder.withPayload(payload.getBytes(StandardCharsets.UTF_8)).build();
		MyBean actual = (MyBean) this.converter.fromMessage(message, MyBean.class);

		assertNotNull(actual);
		assertEquals("Foo", actual.getName());
	}

	@Test(expected = MessageConversionException.class)
	public void fromMessageInvalidXml() throws Exception {
		String payload = "<myBean><name>Foo</name><myBean>";
		Message<?> message = MessageBuilder.withPayload(payload.getBytes(StandardCharsets.UTF_8)).build();
		this.converter.fromMessage(message, MyBean.class);
	}

	@Test(expected = MessageConversionException.class)
	public void fromMessageValidXmlWithUnknownProperty() throws IOException {
		String payload = "<myBean><age>42</age><myBean>";
		Message<?> message = MessageBuilder.withPayload(payload.getBytes(StandardCharsets.UTF_8)).build();
		this.converter.fromMessage(message, MyBean.class);
	}

	@Test
	public void toMessage() throws Exception {
		MyBean payload = new MyBean();
		payload.setName("Foo");

		Message<?> message = this.converter.toMessage(payload, null);
		assertNotNull(message);
		String actual = new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);

		DifferenceEvaluator ev = chain(Default, downgradeDifferencesToEqual(XML_STANDALONE));
		assertThat(actual, isSimilarTo("<myBean><name>Foo</name></myBean>").withDifferenceEvaluator(ev));
	}


	@XmlRootElement
	public static class MyBean {

		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

}
