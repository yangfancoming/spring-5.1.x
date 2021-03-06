

package org.springframework.transaction.jta;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;

import org.springframework.util.Assert;

/**
 * Adapter for a managed JTA Transaction handle, taking a JTA
 * {@link javax.transaction.TransactionManager} reference and creating a JTA {@link javax.transaction.Transaction} handle for it.
 * @since 3.0.2
 */
public class ManagedTransactionAdapter implements Transaction {

	private final TransactionManager transactionManager;

	/**
	 * Create a new ManagedTransactionAdapter for the given TransactionManager.
	 * @param transactionManager the JTA TransactionManager to wrap
	 */
	public ManagedTransactionAdapter(TransactionManager transactionManager) throws SystemException {
		Assert.notNull(transactionManager, "TransactionManager must not be null");
		this.transactionManager = transactionManager;
	}

	/**
	 * Return the JTA TransactionManager that this adapter delegates to.
	 */
	public final TransactionManager getTransactionManager() {
		return transactionManager;
	}


	@Override
	public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,SecurityException, SystemException {
		transactionManager.commit();
	}

	@Override
	public void rollback() throws SystemException {
		transactionManager.rollback();
	}

	@Override
	public void setRollbackOnly() throws SystemException {
		transactionManager.setRollbackOnly();
	}

	@Override
	public int getStatus() throws SystemException {
		return transactionManager.getStatus();
	}

	@Override
	public boolean enlistResource(XAResource xaRes) throws RollbackException, SystemException {
		return transactionManager.getTransaction().enlistResource(xaRes);
	}

	@Override
	public boolean delistResource(XAResource xaRes, int flag) throws SystemException {
		return transactionManager.getTransaction().delistResource(xaRes, flag);
	}

	@Override
	public void registerSynchronization(Synchronization sync) throws RollbackException, SystemException {
		transactionManager.getTransaction().registerSynchronization(sync);
	}

}
