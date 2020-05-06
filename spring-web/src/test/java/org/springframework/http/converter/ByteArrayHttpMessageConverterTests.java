

package org.springframework.http.converter;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import org.springframework.http.MediaType;
import org.springframework.http.MockHttpInputMessage;
import org.springframework.http.MockHttpOutputMessage;

import static org.junit.Assert.*;

/** @author Arjen Poutsma */
public class ByteArrayHttpMessageConverterTests {

	private ByteArrayHttpMessageConverter converter;

	@Before
	public void setUp() {
		converter = new ByteArrayHttpMessageConverter();
	}

	@Test
	public void canRead() {
		assertTrue(converter.canRead(byte[].class, new MediaType("application", "octet-stream")));
	}

	@Test
	public void canWrite() {
		assertTrue(converter.canWrite(byte[].class, new MediaType("application", "octet-stream")));
		assertTrue(converter.canWrite(byte[].class, MediaType.ALL));
	}

	@Test
	public void read() throws IOException {
		byte[] body = new byte[]{0x1, 0x2};
		MockHttpInputMessage inputMessage = new MockHttpInputMessage(body);
		inputMessage.getHeaders().setContentType(new MediaType("application", "octet-stream"));
		byte[] result = converter.read(byte[].class, inputMessage);
		assertArrayEquals("Invalid result", body, result);
	}

	@Test
	public void write() throws IOException {
		MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
		byte[] body = new byte[]{0x1, 0x2};
		converter.write(body, null, outputMessage);
		assertArrayEquals("Invalid result", body, outputMessage.getBodyAsBytes());
		assertEquals("Invalid content-type", new MediaType("application", "octet-stream"),
				outputMessage.getHeaders().getContentType());
		assertEquals("Invalid content-length", 2, outputMessage.getHeaders().getContentLength());
	}

}
