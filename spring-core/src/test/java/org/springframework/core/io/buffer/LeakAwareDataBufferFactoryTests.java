

package org.springframework.core.io.buffer;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.springframework.core.io.buffer.DataBufferUtils.release;

/**
 * @author Arjen Poutsma
 */
public class LeakAwareDataBufferFactoryTests {

	private final LeakAwareDataBufferFactory bufferFactory = new LeakAwareDataBufferFactory();


	@Test
	public void leak() {
		DataBuffer dataBuffer = this.bufferFactory.allocateBuffer();
		try {
			this.bufferFactory.checkForLeaks();
			fail("AssertionError expected");
		}
		catch (AssertionError expected) {
			// ignore
		}
		finally {
			release(dataBuffer);
		}
	}

	@Test
	public void noLeak() {
		DataBuffer dataBuffer = this.bufferFactory.allocateBuffer();
		release(dataBuffer);
		this.bufferFactory.checkForLeaks();
	}

}