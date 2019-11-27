

package org.springframework.core.io.buffer.support;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

import org.springframework.core.io.buffer.AbstractDataBufferAllocatingTestCase;
import org.springframework.core.io.buffer.DataBuffer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


public class DataBufferTestUtilsTests extends AbstractDataBufferAllocatingTestCase {

	@Test
	public void dumpBytes() {
		DataBuffer buffer = this.bufferFactory.allocateBuffer(4);
		byte[] source = {'a', 'b', 'c', 'd'};
		buffer.write(source);

		byte[] result = DataBufferTestUtils.dumpBytes(buffer);

		assertArrayEquals(source, result);

		release(buffer);
	}

	@Test
	public void dumpString() {
		DataBuffer buffer = this.bufferFactory.allocateBuffer(4);
		String source = "abcd";
		buffer.write(source.getBytes(StandardCharsets.UTF_8));

		String result = DataBufferTestUtils.dumpString(buffer, StandardCharsets.UTF_8);

		assertEquals(source, result);

		release(buffer);
	}

}
