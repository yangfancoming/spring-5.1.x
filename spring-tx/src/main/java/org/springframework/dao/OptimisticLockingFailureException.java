

package org.springframework.dao;

import org.springframework.lang.Nullable;

/**
 * Exception thrown on an optimistic locking violation.
 *
 * This exception will be thrown either by O/R mapping tools
 * or by custom DAO implementations. Optimistic locking failure
 * is typically <i>not</i> detected by the database itself.
 *
 * @author Rod Johnson
 * @see PessimisticLockingFailureException
 */
@SuppressWarnings("serial")
public class OptimisticLockingFailureException extends ConcurrencyFailureException {

	/**
	 * Constructor for OptimisticLockingFailureException.
	 * @param msg the detail message
	 */
	public OptimisticLockingFailureException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for OptimisticLockingFailureException.
	 * @param msg the detail message
	 * @param cause the root cause from the data access API in use
	 */
	public OptimisticLockingFailureException(String msg, @Nullable Throwable cause) {
		super(msg, cause);
	}

}
