

package org.springframework.util.xml;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.Socket;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmlunit.util.Predicate;

import static org.junit.Assert.*;
import static org.xmlunit.matchers.CompareMatcher.*;


public abstract class AbstractStaxHandlerTestCase {

	private static final String COMPLEX_XML =
			"<?xml version='1.0' encoding='UTF-8'?>" +
					"<!DOCTYPE beans PUBLIC \"-//SPRING//DTD BEAN 2.0//EN\" \"https://www.springframework.org/dtd/spring-beans-2.0.dtd\">" +
					"<?pi content?><root xmlns='namespace'><prefix:child xmlns:prefix='namespace2' prefix:attr='value'>characters <![CDATA[cdata]]></prefix:child>" +
					"<!-- comment -->" +
					"</root>";

	private static final String SIMPLE_XML = "<?xml version='1.0' encoding='UTF-8'?>" +
					"<?pi content?><root xmlns='namespace'><prefix:child xmlns:prefix='namespace2' prefix:attr='value'>content</prefix:child>" +
					"</root>";

	private static final Predicate<Node> nodeFilter = (n -> n.getNodeType() != Node.COMMENT_NODE && n.getNodeType() != Node.DOCUMENT_TYPE_NODE && n.getNodeType() != Node.PROCESSING_INSTRUCTION_NODE);

	private XMLReader xmlReader;

	@Before
	@SuppressWarnings("deprecation")  // on JDK 9
	public void createXMLReader() throws Exception {
		xmlReader = org.xml.sax.helpers.XMLReaderFactory.createXMLReader();
	}

	@Test
	public void noNamespacePrefixes() throws Exception {
		Assume.assumeTrue(wwwSpringframeworkOrgIsAccessible());
		StringWriter stringWriter = new StringWriter();
		AbstractStaxHandler handler = createStaxHandler(new StreamResult(stringWriter));
		xmlReader.setContentHandler(handler);
		xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
		xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);
		xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
		xmlReader.parse(new InputSource(new StringReader(COMPLEX_XML)));
		assertThat(stringWriter.toString(), isSimilarTo(COMPLEX_XML).withNodeFilter(nodeFilter));
	}

	private static boolean wwwSpringframeworkOrgIsAccessible() {
		try {
			new Socket("www.springframework.org", 80).close();
		}catch (Exception e) {
			return false;
		}
		return true;
	}

	@Test
	public void namespacePrefixes() throws Exception {
		Assume.assumeTrue(wwwSpringframeworkOrgIsAccessible());
		StringWriter stringWriter = new StringWriter();
		AbstractStaxHandler handler = createStaxHandler(new StreamResult(stringWriter));
		xmlReader.setContentHandler(handler);
		xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
		xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);
		xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
		xmlReader.parse(new InputSource(new StringReader(COMPLEX_XML)));
		assertThat(stringWriter.toString(), isSimilarTo(COMPLEX_XML).withNodeFilter(nodeFilter));
	}

	@Test
	public void noNamespacePrefixesDom() throws Exception {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document expected = documentBuilder.parse(new InputSource(new StringReader(SIMPLE_XML)));
		Document result = documentBuilder.newDocument();
		AbstractStaxHandler handler = createStaxHandler(new DOMResult(result));
		xmlReader.setContentHandler(handler);
		xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
		xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);
		xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
		xmlReader.parse(new InputSource(new StringReader(SIMPLE_XML)));
		assertThat(result, isSimilarTo(expected).withNodeFilter(nodeFilter));
	}

	@Test
	public void namespacePrefixesDom() throws Exception {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document expected = documentBuilder.parse(new InputSource(new StringReader(SIMPLE_XML)));
		Document result = documentBuilder.newDocument();
		AbstractStaxHandler handler = createStaxHandler(new DOMResult(result));
		xmlReader.setContentHandler(handler);
		xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
		xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);
		xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
		xmlReader.parse(new InputSource(new StringReader(SIMPLE_XML)));
		assertThat(expected, isSimilarTo(result).withNodeFilter(nodeFilter));
	}

	protected abstract AbstractStaxHandler createStaxHandler(Result result) throws XMLStreamException;

}
