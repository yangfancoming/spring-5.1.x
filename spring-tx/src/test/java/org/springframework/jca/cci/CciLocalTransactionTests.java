

package org.springframework.jca.cci;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.Interaction;
import javax.resource.cci.InteractionSpec;
import javax.resource.cci.LocalTransaction;
import javax.resource.cci.Record;

import org.junit.Test;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jca.cci.connection.CciLocalTransactionManager;
import org.springframework.jca.cci.core.CciTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

/**
 * @author Thierry Templier

 */
public class CciLocalTransactionTests {

	/**
	 * Test if a transaction ( begin / commit ) is executed on the
	 * LocalTransaction when CciLocalTransactionManager is specified as
	 * transaction manager.
	 */
	@Test
	public void testLocalTransactionCommit() throws ResourceException {
		final ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
		Connection connection = mock(Connection.class);
		Interaction interaction = mock(Interaction.class);
		LocalTransaction localTransaction = mock(LocalTransaction.class);
		final Record record = mock(Record.class);
		final InteractionSpec interactionSpec = mock(InteractionSpec.class);

		given(connectionFactory.getConnection()).willReturn(connection);
		given(connection.getLocalTransaction()).willReturn(localTransaction);
		given(connection.createInteraction()).willReturn(interaction);
		given(interaction.execute(interactionSpec, record, record)).willReturn(true);
		given(connection.getLocalTransaction()).willReturn(localTransaction);

		CciLocalTransactionManager tm = new CciLocalTransactionManager();
		tm.setConnectionFactory(connectionFactory);
		TransactionTemplate tt = new TransactionTemplate(tm);

		tt.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				assertTrue("Has thread connection", TransactionSynchronizationManager.hasResource(connectionFactory));
				CciTemplate ct = new CciTemplate(connectionFactory);
				ct.execute(interactionSpec, record, record);
			}
		});

		verify(localTransaction).begin();
		verify(interaction).close();
		verify(localTransaction).commit();
		verify(connection).close();
	}

	/**
	 * Test if a transaction ( begin / rollback ) is executed on the
	 * LocalTransaction when CciLocalTransactionManager is specified as
	 * transaction manager and a non-checked exception is thrown.
	 */
	@Test
	public void testLocalTransactionRollback() throws ResourceException {
		final ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
		Connection connection = mock(Connection.class);
		Interaction interaction = mock(Interaction.class);
		LocalTransaction localTransaction = mock(LocalTransaction.class);
		final Record record = mock(Record.class);
		final InteractionSpec interactionSpec = mock(InteractionSpec.class);

		given(connectionFactory.getConnection()).willReturn(connection);
		given(connection.getLocalTransaction()).willReturn(localTransaction);
		given(connection.createInteraction()).willReturn(interaction);
		given(interaction.execute(interactionSpec, record, record)).willReturn(true);
		given(connection.getLocalTransaction()).willReturn(localTransaction);

		CciLocalTransactionManager tm = new CciLocalTransactionManager();
		tm.setConnectionFactory(connectionFactory);
		TransactionTemplate tt = new TransactionTemplate(tm);

		try {
			tt.execute(new TransactionCallback<Void>() {
				@Override
				public Void doInTransaction(TransactionStatus status) {
					assertTrue("Has thread connection", TransactionSynchronizationManager.hasResource(connectionFactory));
					CciTemplate ct = new CciTemplate(connectionFactory);
					ct.execute(interactionSpec, record, record);
					throw new DataRetrievalFailureException("error");
				}
			});
		}
		catch (Exception ex) {
		}

		verify(localTransaction).begin();
		verify(interaction).close();
		verify(localTransaction).rollback();
		verify(connection).close();
	}
}
