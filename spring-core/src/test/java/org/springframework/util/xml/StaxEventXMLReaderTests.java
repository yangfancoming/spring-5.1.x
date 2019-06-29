

package org.springframework.util.xml;

import java.io.InputStream;
import java.io.StringReader;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;

import static org.mockito.Mockito.*;

public class StaxEventXMLReaderTests extends AbstractStaxXMLReaderTestCase {

	public static final String CONTENT = "<root xmlns='http://springframework.org/spring-ws'><child/></root>";

	@Override
	protected AbstractStaxXMLReader createStaxXmlReader(InputStream inputStream) throws XMLStreamException {
		return new StaxEventXMLReader(inputFactory.createXMLEventReader(inputStream));
	}

	@Test
	public void partial() throws Exception {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLEventReader eventReader = inputFactory.createXMLEventReader(new StringReader(CONTENT));
		eventReader.nextTag();  // skip to root
		StaxEventXMLReader xmlReader = new StaxEventXMLReader(eventReader);
		ContentHandler contentHandler = mock(ContentHandler.class);
		xmlReader.setContentHandler(contentHandler);
		xmlReader.parse(new InputSource());
		verify(contentHandler).startDocument();
		verify(contentHandler).startElement(eq("http://springframework.org/spring-ws"), eq("child"), eq("child"), any(Attributes.class));
		verify(contentHandler).endElement("http://springframework.org/spring-ws", "child", "child");
		verify(contentHandler).endDocument();
	}

}
