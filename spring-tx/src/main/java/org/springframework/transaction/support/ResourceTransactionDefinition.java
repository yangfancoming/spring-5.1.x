

package org.springframework.transaction.support;

import org.springframework.transaction.TransactionDefinition;

/**
 * Extended variant of {@link TransactionDefinition}, indicating a resource transaction
 * and in particular whether the transactional resource is ready for local optimizations.
 *

 * @since 5.1
 * @see ResourceTransactionManager
 */
public interface ResourceTransactionDefinition extends TransactionDefinition {

	/**
	 * Determine whether the transactional resource is ready for local optimizations.
	 * @return {@code true} if the resource is known to be entirely transaction-local,
	 * not affecting any operations outside of the scope of the current transaction
	 * @see #isReadOnly()
	 */
	boolean isLocalResource();

}
