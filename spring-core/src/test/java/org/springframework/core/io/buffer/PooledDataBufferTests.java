

package org.springframework.core.io.buffer;

import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Arjen Poutsma
 */
@RunWith(Parameterized.class)
public class PooledDataBufferTests {

	@Parameterized.Parameter
	public DataBufferFactory dataBufferFactory;

	@Parameterized.Parameters(name = "{0}")
	public static Object[][] buffers() {

		return new Object[][]{
				{new NettyDataBufferFactory(new UnpooledByteBufAllocator(true))},
				{new NettyDataBufferFactory(new UnpooledByteBufAllocator(false))},
				{new NettyDataBufferFactory(new PooledByteBufAllocator(true))},
				{new NettyDataBufferFactory(new PooledByteBufAllocator(false))}};
	}

	private PooledDataBuffer createDataBuffer(int capacity) {
		return (PooledDataBuffer) dataBufferFactory.allocateBuffer(capacity);
	}

	@Test
	public void retainAndRelease() {
		PooledDataBuffer buffer = createDataBuffer(1);
		buffer.write((byte) 'a');

		buffer.retain();
		boolean result = buffer.release();
		assertFalse(result);
		result = buffer.release();
		assertTrue(result);
	}

	@Test(expected = IllegalStateException.class)
	public void tooManyReleases() {
		PooledDataBuffer buffer = createDataBuffer(1);
		buffer.write((byte) 'a');

		buffer.release();
		buffer.release();
	}


}
