

package org.springframework.test.context.transaction;

import org.springframework.core.NamedInheritableThreadLocal;
import org.springframework.lang.Nullable;

/**
 * {@link InheritableThreadLocal}-based holder for the current {@link TransactionContext}.
 *
 * @author Sam Brannen
 * @since 4.1
 */
final class TransactionContextHolder {

	private static final ThreadLocal<TransactionContext> currentTransactionContext =
			new NamedInheritableThreadLocal<>("Test Transaction Context");


	private TransactionContextHolder() {
	}


	static void setCurrentTransactionContext(TransactionContext transactionContext) {
		currentTransactionContext.set(transactionContext);
	}

	@Nullable
	static TransactionContext getCurrentTransactionContext() {
		return currentTransactionContext.get();
	}

	@Nullable
	static TransactionContext removeCurrentTransactionContext() {
		TransactionContext transactionContext = currentTransactionContext.get();
		currentTransactionContext.remove();
		return transactionContext;
	}

}
