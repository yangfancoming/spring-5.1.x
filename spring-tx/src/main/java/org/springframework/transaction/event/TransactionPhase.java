

package org.springframework.transaction.event;

import org.springframework.transaction.support.TransactionSynchronization;

/**
 * The phase at which a transactional event listener applies.

 * @since 4.2
 * @see TransactionalEventListener
 */
public enum TransactionPhase {

	/**
	 * Fire the event before transaction commit.
	 * @see TransactionSynchronization#beforeCommit(boolean)
	 * // 指定目标方法在事务commit之前执行
	 */
	BEFORE_COMMIT,

	/**
	 * Fire the event after the commit has completed successfully.
	 * Note: This is a specialization of {@link #AFTER_COMPLETION} and
	 * therefore executes in the same after-completion sequence of events,
	 * (and not in {@link TransactionSynchronization#afterCommit()}).
	 * @see TransactionSynchronization#afterCompletion(int)
	 * @see TransactionSynchronization#STATUS_COMMITTED
	 *  指定目标方法在事务commit之后执行
	 */
	AFTER_COMMIT,

	/**
	 * Fire the event if the transaction has rolled back.
	 * Note: This is a specialization of {@link #AFTER_COMPLETION} and
	 * therefore executes in the same after-completion sequence of events.
	 * @see TransactionSynchronization#afterCompletion(int)
	 * @see TransactionSynchronization#STATUS_ROLLED_BACK
	 * 指定目标方法在事务rollback之后执行
	 */
	AFTER_ROLLBACK,

	/**
	 * Fire the event after the transaction has completed.
	 * For more fine-grained events, use {@link #AFTER_COMMIT} or
	 * {@link #AFTER_ROLLBACK} to intercept transaction commit
	 * or rollback, respectively.
	 * @see TransactionSynchronization#afterCompletion(int)
	 * 指定目标方法在事务完成时执行，这里的完成是指无论事务是成功提交还是事务回滚了
	 */
	AFTER_COMPLETION

}
