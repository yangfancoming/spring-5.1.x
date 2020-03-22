

package org.springframework.beans.propertyeditors;

import java.util.ResourceBundle;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link ResourceBundleEditor} class.
 */
public class ResourceBundleEditorTests {

	private static final String BASE_NAME = ResourceBundleEditorTests.class.getName();

	private static final String MESSAGE_KEY = "punk";


	@Test
	public void testSetAsTextWithJustBaseName() throws Exception {
		ResourceBundleEditor editor = new ResourceBundleEditor();
		editor.setAsText(BASE_NAME);
		Object value = editor.getValue();
		assertNotNull("Returned ResourceBundle was null (must not be for valid setAsText(..) call).", value);
		assertTrue("Returned object was not a ResourceBundle (must be for valid setAsText(..) call).",
				value instanceof ResourceBundle);
		ResourceBundle bundle = (ResourceBundle) value;
		String string = bundle.getString(MESSAGE_KEY);
		assertEquals(MESSAGE_KEY, string);
	}

	@Test
	public void testSetAsTextWithBaseNameThatEndsInDefaultSeparator() throws Exception {
		ResourceBundleEditor editor = new ResourceBundleEditor();
		editor.setAsText(BASE_NAME + "_");
		Object value = editor.getValue();
		assertNotNull("Returned ResourceBundle was null (must not be for valid setAsText(..) call).", value);
		assertTrue("Returned object was not a ResourceBundle (must be for valid setAsText(..) call).",
				value instanceof ResourceBundle);
		ResourceBundle bundle = (ResourceBundle) value;
		String string = bundle.getString(MESSAGE_KEY);
		assertEquals(MESSAGE_KEY, string);
	}

	@Test
	public void testSetAsTextWithBaseNameAndLanguageCode() throws Exception {
		ResourceBundleEditor editor = new ResourceBundleEditor();
		editor.setAsText(BASE_NAME + "Lang" + "_en");
		Object value = editor.getValue();
		assertNotNull("Returned ResourceBundle was null (must not be for valid setAsText(..) call).", value);
		assertTrue("Returned object was not a ResourceBundle (must be for valid setAsText(..) call).",
				value instanceof ResourceBundle);
		ResourceBundle bundle = (ResourceBundle) value;
		String string = bundle.getString(MESSAGE_KEY);
		assertEquals("yob", string);
	}

	@Test
	public void testSetAsTextWithBaseNameLanguageAndCountryCode() throws Exception {
		ResourceBundleEditor editor = new ResourceBundleEditor();
		editor.setAsText(BASE_NAME + "LangCountry" + "_en_GB");
		Object value = editor.getValue();
		assertNotNull("Returned ResourceBundle was null (must not be for valid setAsText(..) call).", value);
		assertTrue("Returned object was not a ResourceBundle (must be for valid setAsText(..) call).",
				value instanceof ResourceBundle);
		ResourceBundle bundle = (ResourceBundle) value;
		String string = bundle.getString(MESSAGE_KEY);
		assertEquals("chav", string);
	}

	@Test
	public void testSetAsTextWithTheKitchenSink() throws Exception {
		ResourceBundleEditor editor = new ResourceBundleEditor();
		editor.setAsText(BASE_NAME + "LangCountryDialect" + "_en_GB_GLASGOW");
		Object value = editor.getValue();
		assertNotNull("Returned ResourceBundle was null (must not be for valid setAsText(..) call).", value);
		assertTrue("Returned object was not a ResourceBundle (must be for valid setAsText(..) call).",
				value instanceof ResourceBundle);
		ResourceBundle bundle = (ResourceBundle) value;
		String string = bundle.getString(MESSAGE_KEY);
		assertEquals("ned", string);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetAsTextWithNull() throws Exception {
		ResourceBundleEditor editor = new ResourceBundleEditor();
		editor.setAsText(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetAsTextWithEmptyString() throws Exception {
		ResourceBundleEditor editor = new ResourceBundleEditor();
		editor.setAsText("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetAsTextWithWhiteSpaceString() throws Exception {
		ResourceBundleEditor editor = new ResourceBundleEditor();
		editor.setAsText("   ");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetAsTextWithJustSeparatorString() throws Exception {
		ResourceBundleEditor editor = new ResourceBundleEditor();
		editor.setAsText("_");
	}

}
