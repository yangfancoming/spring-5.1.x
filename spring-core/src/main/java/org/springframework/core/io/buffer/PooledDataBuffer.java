

package org.springframework.core.io.buffer;

/**
 * Extension of {@link DataBuffer} that allows for buffer that share
 * a memory pool. Introduces methods for reference counting.
 * @since 5.0
 */
public interface PooledDataBuffer extends DataBuffer {

	/**
	 * Return {@code true} if this buffer is allocated;
	 * {@code false} if it has been deallocated.
	 * @since 5.1
	 */
	boolean isAllocated();

	/**
	 * Increase the reference count for this buffer by one.
	 * @return this buffer
	 */
	PooledDataBuffer retain();

	/**
	 * Decrease the reference count for this buffer by one,
	 * and deallocate it once the count reaches zero.
	 * @return {@code true} if the buffer was deallocated;
	 * {@code false} otherwise
	 */
	boolean release();

}
