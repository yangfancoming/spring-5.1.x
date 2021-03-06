

package org.springframework.transaction.support;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.junit.Test;

import org.springframework.tests.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.util.SerializationTestUtils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class JtaTransactionManagerSerializationTests {

	@Test
	public void serializable() throws Exception {
		UserTransaction ut1 = mock(UserTransaction.class);
		UserTransaction ut2 = mock(UserTransaction.class);
		TransactionManager tm = mock(TransactionManager.class);

		JtaTransactionManager jtam = new JtaTransactionManager();
		jtam.setUserTransaction(ut1);
		jtam.setTransactionManager(tm);
		jtam.setRollbackOnCommitFailure(true);
		jtam.afterPropertiesSet();

		SimpleNamingContextBuilder jndiEnv = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
		jndiEnv.bind(JtaTransactionManager.DEFAULT_USER_TRANSACTION_NAME, ut2);
		JtaTransactionManager serializedJtatm = (JtaTransactionManager) SerializationTestUtils.serializeAndDeserialize(jtam);
		// should do client-side lookup
		assertNotNull("Logger must survive serialization",
				serializedJtatm.logger);
		assertTrue("UserTransaction looked up on client", serializedJtatm
				.getUserTransaction() == ut2);
		assertNull("TransactionManager didn't survive", serializedJtatm
				.getTransactionManager());
		assertEquals(true, serializedJtatm.isRollbackOnCommitFailure());
	}

}
