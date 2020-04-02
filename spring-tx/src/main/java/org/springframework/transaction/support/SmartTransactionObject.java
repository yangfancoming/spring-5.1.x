

package org.springframework.transaction.support;

import java.io.Flushable;

/**
 * Interface to be implemented by transaction objects that are able to
 * return an internal rollback-only marker, typically from a another
 * transaction that has participated and marked it as rollback-only.
 *
 * Autodetected by DefaultTransactionStatus, to always return a
 * current rollbackOnly flag even if not resulting from the current
 * TransactionStatus.
 *

 * @since 1.1
 * @see DefaultTransactionStatus#isRollbackOnly
 */
public interface SmartTransactionObject extends Flushable {

	/**
	 * Return whether the transaction is internally marked as rollback-only.
	 * Can, for example, check the JTA UserTransaction.
	 * @see javax.transaction.UserTransaction#getStatus
	 * @see javax.transaction.Status#STATUS_MARKED_ROLLBACK
	 */
	boolean isRollbackOnly();

	/**
	 * Flush the underlying sessions to the datastore, if applicable:
	 * for example, all affected Hibernate/JPA sessions.
	 */
	@Override
	void flush();

}
