

package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditor;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link ByteArrayPropertyEditor} class.
 *
 * @author Rick Evans
 */
public class ByteArrayPropertyEditorTests {

	private final PropertyEditor byteEditor = new ByteArrayPropertyEditor();

	@Test
	public void sunnyDaySetAsText() throws Exception {
		final String text = "Hideous towns make me throw... up";
		byteEditor.setAsText(text);

		Object value = byteEditor.getValue();
		assertNotNull(value);
		assertTrue(value instanceof byte[]);
		byte[] bytes = (byte[]) value;
		for (int i = 0; i < text.length(); ++i) {
			assertEquals("cyte[] differs at index '" + i + "'", text.charAt(i), bytes[i]);
		}
		assertEquals(text, byteEditor.getAsText());
	}

	@Test
	public void getAsTextReturnsEmptyStringIfValueIsNull() throws Exception {
		assertEquals("", byteEditor.getAsText());

		byteEditor.setAsText(null);
		assertEquals("", byteEditor.getAsText());
	}

}
