

package org.springframework.util.xml;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Result;


public class StaxEventHandlerTests extends AbstractStaxHandlerTestCase {

	@Override
	protected AbstractStaxHandler createStaxHandler(Result result) throws XMLStreamException {
		XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
		XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(result);
		return new StaxEventHandler(eventWriter);
	}
}
