

package org.springframework.beans.propertyeditors;

import java.io.Reader;

import org.junit.Test;

import org.springframework.util.ClassUtils;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link ReaderEditor} class.
 *
 * @author Juergen Hoeller
 * @since 4.2
 */
public class ReaderEditorTests {

	@Test(expected = IllegalArgumentException.class)
	public void testCtorWithNullResourceEditor() throws Exception {
		new ReaderEditor(null);
	}

	@Test
	public void testSunnyDay() throws Exception {
		Reader reader = null;
		try {
			String resource = "classpath:" + ClassUtils.classPackageAsResourcePath(getClass()) +
					"/" + ClassUtils.getShortName(getClass()) + ".class";
			ReaderEditor editor = new ReaderEditor();
			editor.setAsText(resource);
			Object value = editor.getValue();
			assertNotNull(value);
			assertTrue(value instanceof Reader);
			reader = (Reader) value;
			assertTrue(reader.ready());
		}
		finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWhenResourceDoesNotExist() throws Exception {
		String resource = "classpath:bingo!";
		ReaderEditor editor = new ReaderEditor();
		editor.setAsText(resource);
	}

	@Test
	public void testGetAsTextReturnsNullByDefault() throws Exception {
		assertNull(new ReaderEditor().getAsText());
		String resource = "classpath:" + ClassUtils.classPackageAsResourcePath(getClass()) +
				"/" + ClassUtils.getShortName(getClass()) + ".class";
		ReaderEditor editor = new ReaderEditor();
		editor.setAsText(resource);
		assertNull(editor.getAsText());
	}

}
