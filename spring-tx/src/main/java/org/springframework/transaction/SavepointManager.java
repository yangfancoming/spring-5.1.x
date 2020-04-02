

package org.springframework.transaction;

/**
 * Interface that specifies an API to programmatically manage transaction
 * savepoints in a generic fashion. Extended by TransactionStatus to
 * expose savepoint management functionality for a specific transaction.
 *
 * Note that savepoints can only work within an active transaction.
 * Just use this programmatic savepoint handling for advanced needs;
 * else, a subtransaction with PROPAGATION_NESTED is preferable.
 *
 * This interface is inspired by JDBC 3.0's Savepoint mechanism
 * but is independent from any specific persistence technology.

 * @since 1.1
 * @see TransactionStatus
 * @see TransactionDefinition#PROPAGATION_NESTED
 * @see java.sql.Savepoint
 */
public interface SavepointManager {

	/**
	 * Create a new savepoint. You can roll back to a specific savepoint
	 * via {@code rollbackToSavepoint}, and explicitly release a savepoint
	 * that you don't need anymore via {@code releaseSavepoint}.
	 * Note that most transaction managers will automatically release
	 * savepoints at transaction completion.
	 * @return a savepoint object, to be passed into
	 * {@link #rollbackToSavepoint} or {@link #releaseSavepoint}
	 * @throws NestedTransactionNotSupportedException if the underlying
	 * transaction does not support savepoints
	 * @throws TransactionException if the savepoint could not be created,
	 * for example because the transaction is not in an appropriate state
	 * @see java.sql.Connection#setSavepoint
	 * // 创建一个新的保存点
	 */
	Object createSavepoint() throws TransactionException;

	/**
	 * Roll back to the given savepoint.
	 * The savepoint will <i>not</i> be automatically released afterwards.
	 * You may explicitly call {@link #releaseSavepoint(Object)} or rely on
	 * automatic release on transaction completion.
	 * @param savepoint the savepoint to roll back to
	 * @throws NestedTransactionNotSupportedException if the underlying
	 * transaction does not support savepoints
	 * @throws TransactionException if the rollback failed
	 * @see java.sql.Connection#rollback(java.sql.Savepoint)
	 *    // 回滚到给定的保存点。
	 *     // 注意：调用此方法回滚到给定的保存点之后，不会自动释放保存点，
	 *     // 可以通过调用releaseSavepoint方法释放保存点。
	 */
	void rollbackToSavepoint(Object savepoint) throws TransactionException;

	/**
	 * Explicitly release the given savepoint.
	 * Note that most transaction managers will automatically release
	 * savepoints on transaction completion.
	 * Implementations should fail as silently as possible if proper
	 * resource cleanup will eventually happen at transaction completion.
	 * @param savepoint the savepoint to release
	 * @throws NestedTransactionNotSupportedException if the underlying
	 * transaction does not support savepoints
	 * @throws TransactionException if the release failed
	 * @see java.sql.Connection#releaseSavepoint
	 * // 显式释放给定的保存点。（大多数事务管理器将在事务完成时自动释放保存点）
	 */
	void releaseSavepoint(Object savepoint) throws TransactionException;

}
