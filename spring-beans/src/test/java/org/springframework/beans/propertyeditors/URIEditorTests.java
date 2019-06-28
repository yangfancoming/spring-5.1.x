

package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditor;
import java.net.URI;

import org.junit.Test;

import org.springframework.util.ClassUtils;

import static org.junit.Assert.*;

/**
 * @author Juergen Hoeller
 * @author Arjen Poutsma
 */
public class URIEditorTests {

	@Test
	public void standardURI() throws Exception {
		doTestURI("mailto:juergen.hoeller@interface21.com");
	}

	@Test
	public void withNonExistentResource() throws Exception {
		doTestURI("gonna:/freak/in/the/morning/freak/in/the.evening");
	}

	@Test
	public void standardURL() throws Exception {
		doTestURI("https://www.springframework.org");
	}

	@Test
	public void standardURLWithFragment() throws Exception {
		doTestURI("https://www.springframework.org#1");
	}

	@Test
	public void standardURLWithWhitespace() throws Exception {
		PropertyEditor uriEditor = new URIEditor();
		uriEditor.setAsText("  https://www.springframework.org  ");
		Object value = uriEditor.getValue();
		assertTrue(value instanceof URI);
		URI uri = (URI) value;
		assertEquals("https://www.springframework.org", uri.toString());
	}

	@Test
	public void classpathURL() throws Exception {
		PropertyEditor uriEditor = new URIEditor(getClass().getClassLoader());
		uriEditor.setAsText("classpath:" + ClassUtils.classPackageAsResourcePath(getClass()) +
				"/" + ClassUtils.getShortName(getClass()) + ".class");
		Object value = uriEditor.getValue();
		assertTrue(value instanceof URI);
		URI uri = (URI) value;
		assertEquals(uri.toString(), uriEditor.getAsText());
		assertTrue(!uri.getScheme().startsWith("classpath"));
	}

	@Test
	public void classpathURLWithWhitespace() throws Exception {
		PropertyEditor uriEditor = new URIEditor(getClass().getClassLoader());
		uriEditor.setAsText("  classpath:" + ClassUtils.classPackageAsResourcePath(getClass()) +
				"/" + ClassUtils.getShortName(getClass()) + ".class  ");
		Object value = uriEditor.getValue();
		assertTrue(value instanceof URI);
		URI uri = (URI) value;
		assertEquals(uri.toString(), uriEditor.getAsText());
		assertTrue(!uri.getScheme().startsWith("classpath"));
	}

	@Test
	public void classpathURLAsIs() throws Exception {
		PropertyEditor uriEditor = new URIEditor();
		uriEditor.setAsText("classpath:test.txt");
		Object value = uriEditor.getValue();
		assertTrue(value instanceof URI);
		URI uri = (URI) value;
		assertEquals(uri.toString(), uriEditor.getAsText());
		assertTrue(uri.getScheme().startsWith("classpath"));
	}

	@Test
	public void setAsTextWithNull() throws Exception {
		PropertyEditor uriEditor = new URIEditor();
		uriEditor.setAsText(null);
		assertNull(uriEditor.getValue());
		assertEquals("", uriEditor.getAsText());
	}

	@Test
	public void getAsTextReturnsEmptyStringIfValueNotSet() throws Exception {
		PropertyEditor uriEditor = new URIEditor();
		assertEquals("", uriEditor.getAsText());
	}

	@Test
	public void encodeURI() throws Exception {
		PropertyEditor uriEditor = new URIEditor();
		uriEditor.setAsText("https://example.com/spaces and \u20AC");
		Object value = uriEditor.getValue();
		assertTrue(value instanceof URI);
		URI uri = (URI) value;
		assertEquals(uri.toString(), uriEditor.getAsText());
		assertEquals("https://example.com/spaces%20and%20%E2%82%AC", uri.toASCIIString());
	}

	@Test
	public void encodeAlreadyEncodedURI() throws Exception {
		PropertyEditor uriEditor = new URIEditor(false);
		uriEditor.setAsText("https://example.com/spaces%20and%20%E2%82%AC");
		Object value = uriEditor.getValue();
		assertTrue(value instanceof URI);
		URI uri = (URI) value;
		assertEquals(uri.toString(), uriEditor.getAsText());
		assertEquals("https://example.com/spaces%20and%20%E2%82%AC", uri.toASCIIString());
	}


	private void doTestURI(String uriSpec) {
		PropertyEditor uriEditor = new URIEditor();
		uriEditor.setAsText(uriSpec);
		Object value = uriEditor.getValue();
		assertTrue(value instanceof URI);
		URI uri = (URI) value;
		assertEquals(uriSpec, uri.toString());
	}

}
