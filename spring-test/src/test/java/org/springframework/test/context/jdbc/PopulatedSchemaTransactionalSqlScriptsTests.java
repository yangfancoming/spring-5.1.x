

package org.springframework.test.context.jdbc;

import org.junit.Test;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;

import static org.junit.Assert.*;

/**
 * Transactional integration tests that verify rollback semantics for
 * {@link Sql @Sql} support.
 *
 * @author Sam Brannen
 * @since 4.1
 */
@ContextConfiguration(classes = PopulatedSchemaDatabaseConfig.class)
@DirtiesContext
public class PopulatedSchemaTransactionalSqlScriptsTests extends AbstractTransactionalJUnit4SpringContextTests {

	@BeforeTransaction
	@AfterTransaction
	public void verifyPreAndPostTransactionDatabaseState() {
		assertNumUsers(0);
	}

	@Test
	@SqlGroup(@Sql("data-add-dogbert.sql"))
	public void methodLevelScripts() {
		assertNumUsers(1);
	}

	protected void assertNumUsers(int expected) {
		assertEquals("Number of rows in the 'user' table.", expected, countRowsInTable("user"));
	}

}
