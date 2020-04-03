

package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditor;
import java.net.URL;

import org.junit.Test;

import org.springframework.util.ClassUtils;

import static org.junit.Assert.*;


public class URLEditorTests {

	@Test(expected = IllegalArgumentException.class)
	public void testCtorWithNullResourceEditor() throws Exception {
		new URLEditor(null);
	}

	@Test
	public void testStandardURI() throws Exception {
		PropertyEditor urlEditor = new URLEditor();
		urlEditor.setAsText("mailto:juergen.hoeller@interface21.com");
		Object value = urlEditor.getValue();
		assertTrue(value instanceof URL);
		URL url = (URL) value;
		assertEquals(url.toExternalForm(), urlEditor.getAsText());
	}

	@Test
	public void testStandardURL() throws Exception {
		PropertyEditor urlEditor = new URLEditor();
		urlEditor.setAsText("https://www.springframework.org");
		Object value = urlEditor.getValue();
		assertTrue(value instanceof URL);
		URL url = (URL) value;
		assertEquals(url.toExternalForm(), urlEditor.getAsText());
	}

	@Test
	public void testClasspathURL() throws Exception {
		PropertyEditor urlEditor = new URLEditor();
		urlEditor.setAsText("classpath:" + ClassUtils.classPackageAsResourcePath(getClass()) +
				"/" + ClassUtils.getShortName(getClass()) + ".class");
		Object value = urlEditor.getValue();
		assertTrue(value instanceof URL);
		URL url = (URL) value;
		assertEquals(url.toExternalForm(), urlEditor.getAsText());
		assertTrue(!url.getProtocol().startsWith("classpath"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWithNonExistentResource() throws Exception {
		PropertyEditor urlEditor = new URLEditor();
		urlEditor.setAsText("gonna:/freak/in/the/morning/freak/in/the.evening");
	}

	@Test
	public void testSetAsTextWithNull() throws Exception {
		PropertyEditor urlEditor = new URLEditor();
		urlEditor.setAsText(null);
		assertNull(urlEditor.getValue());
		assertEquals("", urlEditor.getAsText());
	}

	@Test
	public void testGetAsTextReturnsEmptyStringIfValueNotSet() throws Exception {
		PropertyEditor urlEditor = new URLEditor();
		assertEquals("", urlEditor.getAsText());
	}

}
