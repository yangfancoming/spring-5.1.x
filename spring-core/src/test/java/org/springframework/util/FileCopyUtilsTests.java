

package org.springframework.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the FileCopyUtils class.
 *

 * @since 12.03.2005
 */
public class FileCopyUtilsTests {

	@Test
	public void copyFromInputStream() throws IOException {
		byte[] content = "content".getBytes();
		ByteArrayInputStream in = new ByteArrayInputStream(content);
		ByteArrayOutputStream out = new ByteArrayOutputStream(content.length);
		int count = FileCopyUtils.copy(in, out);
		assertEquals(content.length, count);
		assertTrue(Arrays.equals(content, out.toByteArray()));
	}

	@Test
	public void copyFromByteArray() throws IOException {
		byte[] content = "content".getBytes();
		ByteArrayOutputStream out = new ByteArrayOutputStream(content.length);
		FileCopyUtils.copy(content, out);
		assertTrue(Arrays.equals(content, out.toByteArray()));
	}

	@Test
	public void copyToByteArray() throws IOException {
		byte[] content = "content".getBytes();
		ByteArrayInputStream in = new ByteArrayInputStream(content);
		byte[] result = FileCopyUtils.copyToByteArray(in);
		assertTrue(Arrays.equals(content, result));
	}

	@Test
	public void copyFromReader() throws IOException {
		String content = "content";
		StringReader in = new StringReader(content);
		StringWriter out = new StringWriter();
		int count = FileCopyUtils.copy(in, out);
		assertEquals(content.length(), count);
		assertEquals(content, out.toString());
	}

	@Test
	public void copyFromString() throws IOException {
		String content = "content";
		StringWriter out = new StringWriter();
		FileCopyUtils.copy(content, out);
		assertEquals(content, out.toString());
	}

	@Test
	public void copyToString() throws IOException {
		String content = "content";
		StringReader in = new StringReader(content);
		String result = FileCopyUtils.copyToString(in);
		assertEquals(content, result);
	}

}
