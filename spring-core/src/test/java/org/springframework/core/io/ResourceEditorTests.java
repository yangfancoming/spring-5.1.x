

package org.springframework.core.io;

import java.beans.PropertyEditor;

import org.junit.Test;

import org.springframework.core.env.StandardEnvironment;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link ResourceEditor} class.
 */
public class ResourceEditorTests {

	@Test
	public void sunnyDay() {
		PropertyEditor editor = new ResourceEditor();
		editor.setAsText("classpath:org/springframework/core/io/ResourceEditorTests.class");
		Resource resource = (Resource) editor.getValue();
		assertNotNull(resource);
		assertTrue(resource.exists());
	}

	@Test(expected = IllegalArgumentException.class)
	public void ctorWithNullCtorArgs() {
		new ResourceEditor(null, null);
	}

	@Test
	public void setAndGetAsTextWithNull() {
		PropertyEditor editor = new ResourceEditor();
		editor.setAsText(null);
		assertEquals("", editor.getAsText());
	}

	@Test
	public void setAndGetAsTextWithWhitespaceResource() {
		PropertyEditor editor = new ResourceEditor();
		editor.setAsText("  ");
		assertEquals("", editor.getAsText());
	}

	@Test
	public void testSystemPropertyReplacement() {
		PropertyEditor editor = new ResourceEditor();
		System.setProperty("test.prop", "foo");
		try {
			editor.setAsText("${test.prop}");
			Resource resolved = (Resource) editor.getValue();
			assertEquals("foo", resolved.getFilename());
		}finally {
			System.getProperties().remove("test.prop");
		}
	}

	@Test
	public void testSystemPropertyReplacementWithUnresolvablePlaceholder() {
		PropertyEditor editor = new ResourceEditor();
		System.setProperty("test.prop", "foo");
		try {
			editor.setAsText("${test.prop}-${bar}");
			Resource resolved = (Resource) editor.getValue();
			assertEquals("foo-${bar}", resolved.getFilename());
		}finally {
			System.getProperties().remove("test.prop");
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testStrictSystemPropertyReplacementWithUnresolvablePlaceholder() {
		PropertyEditor editor = new ResourceEditor(new DefaultResourceLoader(), new StandardEnvironment(), false);
		System.setProperty("test.prop", "foo");
		try {
			editor.setAsText("${test.prop}-${bar}");
		}finally {
			System.getProperties().remove("test.prop");
		}
	}

}
