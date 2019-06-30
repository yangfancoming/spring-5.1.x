

package org.springframework.test.context.jdbc;

import org.junit.Test;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.SqlConfig.TransactionMode;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;

import static org.junit.Assert.*;

/**
 * Transactional integration tests that verify commit semantics for
 * {@link SqlConfig#transactionMode} and {@link TransactionMode#ISOLATED}.
 *
 * @author Sam Brannen
 * @since 4.1
 */
@ContextConfiguration(classes = PopulatedSchemaDatabaseConfig.class)
@DirtiesContext
public class IsolatedTransactionModeSqlScriptsTests extends AbstractTransactionalJUnit4SpringContextTests {

	@BeforeTransaction
	public void beforeTransaction() {
		assertNumUsers(0);
	}

	@Test
	@SqlGroup(@Sql(scripts = "data-add-dogbert.sql", config = @SqlConfig(transactionMode = TransactionMode.ISOLATED)))
	public void methodLevelScripts() {
		assertNumUsers(1);
	}

	@AfterTransaction
	public void afterTransaction() {
		assertNumUsers(1);
	}

	protected void assertNumUsers(int expected) {
		assertEquals("Number of rows in the 'user' table.", expected, countRowsInTable("user"));
	}

}
