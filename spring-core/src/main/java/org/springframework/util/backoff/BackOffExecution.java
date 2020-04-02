

package org.springframework.util.backoff;

/**
 * Represent a particular back-off execution.
 *
 * Implementations do not need to be thread safe.
 *
 * @author Stephane Nicoll
 * @since 4.1
 * @see BackOff
 */
@FunctionalInterface
public interface BackOffExecution {

	/**
	 * Return value of {@link #nextBackOff()} that indicates that the operation
	 * should not be retried.
	 */
	long STOP = -1;

	/**
	 * Return the number of milliseconds to wait before retrying the operation
	 * or {@link #STOP} ({@value #STOP}) to indicate that no further attempt
	 * should be made for the operation.
	 */
	long nextBackOff();

}
