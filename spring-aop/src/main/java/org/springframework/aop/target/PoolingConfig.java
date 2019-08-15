

package org.springframework.aop.target;

/**
 * Config interface for a pooling target source.
 */
public interface PoolingConfig {

	/**
	 * Return the maximum size of the pool.
	 */
	int getMaxSize();

	/**
	 * Return the number of active objects in the pool.
	 * @throws UnsupportedOperationException if not supported by the pool
	 */
	int getActiveCount() throws UnsupportedOperationException;

	/**
	 * Return the number of idle objects in the pool.
	 * @throws UnsupportedOperationException if not supported by the pool
	 */
	int getIdleCount() throws UnsupportedOperationException;

}
