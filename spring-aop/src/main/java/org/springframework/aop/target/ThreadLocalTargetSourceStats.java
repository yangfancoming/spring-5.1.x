

package org.springframework.aop.target;

/**
 * Statistics for a ThreadLocal TargetSource.
 *
 * @author Rod Johnson

 */
public interface ThreadLocalTargetSourceStats {

	/**
	 * Return the number of client invocations.
	 */
	int getInvocationCount();

	/**
	 * Return the number of hits that were satisfied by a thread-bound object.
	 */
	int getHitCount();

	/**
	 * Return the number of thread-bound objects created.
	 */
	int getObjectCount();

}
