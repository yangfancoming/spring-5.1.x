

package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditor;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link CharArrayPropertyEditor} class.
 *
 * @author Rick Evans
 */
public class CharArrayPropertyEditorTests {

	private final PropertyEditor charEditor = new CharArrayPropertyEditor();

	@Test
	public void sunnyDaySetAsText() throws Exception {
		final String text = "Hideous towns make me throw... up";
		charEditor.setAsText(text);

		Object value = charEditor.getValue();
		assertNotNull(value);
		assertTrue(value instanceof char[]);
		char[] chars = (char[]) value;
		for (int i = 0; i < text.length(); ++i) {
			assertEquals("char[] differs at index '" + i + "'", text.charAt(i), chars[i]);
		}
		assertEquals(text, charEditor.getAsText());
	}

	@Test
	public void getAsTextReturnsEmptyStringIfValueIsNull() throws Exception {
		assertEquals("", charEditor.getAsText());

		charEditor.setAsText(null);
		assertEquals("", charEditor.getAsText());
	}

}
