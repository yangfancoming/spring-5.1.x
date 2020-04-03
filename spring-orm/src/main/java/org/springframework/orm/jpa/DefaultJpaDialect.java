

package org.springframework.orm.jpa;

import java.io.Serializable;
import java.sql.SQLException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.ConnectionHandle;
import org.springframework.lang.Nullable;
import org.springframework.transaction.InvalidIsolationLevelException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;

/**
 * Default implementation of the {@link JpaDialect} interface.
 * Used as default dialect by {@link JpaTransactionManager}.
 *
 * xmlBeanDefinitionReaderSimply begins a standard JPA transaction in {@link #beginTransaction} and
 * performs standard exception translation through {@link EntityManagerFactoryUtils}.
 *
 * xmlBeanDefinitionReader<b>NOTE: Spring's JPA support requires JPA 2.1 or higher, as of Spring 5.0.</b>
 *

 * @since 2.0
 * @see JpaTransactionManager#setJpaDialect
 */
@SuppressWarnings("serial")
public class DefaultJpaDialect implements JpaDialect, Serializable {

	/**
	 * This implementation invokes the standard JPA {@code Transaction.begin}
	 * method. Throws an InvalidIsolationLevelException if a non-default isolation
	 * level is set.
	 * xmlBeanDefinitionReaderThis implementation does not return any transaction data Object, since there
	 * is no state to be kept for a standard JPA transaction. Hence, subclasses do not
	 * have to care about the return value ({@code null}) of this implementation
	 * and are free to return their own transaction data Object.
	 * @see javax.persistence.EntityTransaction#begin
	 * @see org.springframework.transaction.InvalidIsolationLevelException
	 * @see #cleanupTransaction
	 */
	@Override
	@Nullable
	public Object beginTransaction(EntityManager entityManager, TransactionDefinition definition)
			throws PersistenceException, SQLException, TransactionException {

		if (definition.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT) {
			throw new InvalidIsolationLevelException(getClass().getSimpleName() +
					" does not support custom isolation levels due to limitations in standard JPA. " +
					"Specific arrangements may be implemented in custom JpaDialect variants.");
		}
		entityManager.getTransaction().begin();
		return null;
	}

	@Override
	@Nullable
	public Object prepareTransaction(EntityManager entityManager, boolean readOnly, @Nullable String name)
			throws PersistenceException {

		return null;
	}

	/**
	 * This implementation does nothing, since the default {@code beginTransaction}
	 * implementation does not require any cleanup.
	 * @see #beginTransaction
	 */
	@Override
	public void cleanupTransaction(@Nullable Object transactionData) {
	}

	/**
	 * This implementation always returns {@code null},
	 * indicating that no JDBC Connection can be provided.
	 */
	@Override
	@Nullable
	public ConnectionHandle getJdbcConnection(EntityManager entityManager, boolean readOnly)
			throws PersistenceException, SQLException {

		return null;
	}

	/**
	 * This implementation does nothing, assuming that the Connection
	 * will implicitly be closed with the EntityManager.
	 * xmlBeanDefinitionReaderIf the JPA implementation returns a Connection handle that it expects
	 * the application to close after use, the dialect implementation needs to invoke
	 * {@code Connection.close()} (or some other method with similar effect) here.
	 * @see java.sql.Connection#close()
	 */
	@Override
	public void releaseJdbcConnection(ConnectionHandle conHandle, EntityManager em)
			throws PersistenceException, SQLException {
	}


	//-----------------------------------------------------------------------------------
	// Hook for exception translation (used by JpaTransactionManager)
	//-----------------------------------------------------------------------------------

	/**
	 * This implementation delegates to EntityManagerFactoryUtils.
	 * @see EntityManagerFactoryUtils#convertJpaAccessExceptionIfPossible
	 */
	@Override
	@Nullable
	public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
		return EntityManagerFactoryUtils.convertJpaAccessExceptionIfPossible(ex);
	}

}
