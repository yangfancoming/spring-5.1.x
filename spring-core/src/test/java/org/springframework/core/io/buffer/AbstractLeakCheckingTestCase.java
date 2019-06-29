

package org.springframework.core.io.buffer;

import org.junit.After;

/**
 * Abstract base class for unit tests that allocate data buffers via a {@link DataBufferFactory}.
 * After each unit test, this base class checks whether all created buffers have been released,
 * throwing an {@link AssertionError} if not.
 *
 * @author Arjen Poutsma
 * @since 5.1.3
 * @see LeakAwareDataBufferFactory
 */
public abstract class AbstractLeakCheckingTestCase {

	/**
	 * The data buffer factory.
	 */
	@SuppressWarnings("ProtectedField")
	protected final LeakAwareDataBufferFactory bufferFactory = new LeakAwareDataBufferFactory();

	/**
	 * Checks whether any of the data buffers created by {@link #bufferFactory} have not been
	 * released, throwing an assertion error if so.
	 */
	@After
	public final void checkForLeaks() {
		this.bufferFactory.checkForLeaks();
	}

}
