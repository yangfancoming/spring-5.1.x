

package org.springframework.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Brian Clozel

 */
public class ResizableByteArrayOutputStreamTests {

	private static final int INITIAL_CAPACITY = 256;

	private ResizableByteArrayOutputStream baos;

	private byte[] helloBytes;


	@Before
	public void setUp() throws Exception {
		this.baos = new ResizableByteArrayOutputStream(INITIAL_CAPACITY);
		this.helloBytes = "Hello World".getBytes("UTF-8");
	}


	@Test
	public void resize() throws Exception {
		assertEquals(INITIAL_CAPACITY, this.baos.capacity());
		this.baos.write(helloBytes);
		int size = 64;
		this.baos.resize(size);
		assertEquals(size, this.baos.capacity());
		assertByteArrayEqualsString(this.baos);
	}

	@Test
	public void autoGrow() {
		assertEquals(INITIAL_CAPACITY, this.baos.capacity());
		for (int i = 0; i < 129; i++) {
			this.baos.write(0);
		}
		assertEquals(256, this.baos.capacity());
	}

	@Test
	public void grow() throws Exception {
		assertEquals(INITIAL_CAPACITY, this.baos.capacity());
		this.baos.write(helloBytes);
		this.baos.grow(1000);
		assertEquals(this.helloBytes.length + 1000, this.baos.capacity());
		assertByteArrayEqualsString(this.baos);
	}

	@Test
	public void write() throws Exception{
		this.baos.write(helloBytes);
		assertByteArrayEqualsString(this.baos);
	}

	@Test(expected = IllegalArgumentException.class)
	public void failResize() throws Exception{
		this.baos.write(helloBytes);
		this.baos.resize(5);
	}


	private void assertByteArrayEqualsString(ResizableByteArrayOutputStream actual) {
		assertArrayEquals(helloBytes, actual.toByteArray());
	}

}
