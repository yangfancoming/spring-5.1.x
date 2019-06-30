

package org.springframework.test.context.jdbc;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runners.MethodSorters;

import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.AfterTransaction;

import static org.junit.Assert.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.*;

/**
 * Transactional integration tests for {@link Sql @Sql} that verify proper
 * support for {@link ExecutionPhase#AFTER_TEST_METHOD}.
 *
 * @author Sam Brannen
 * @since 4.1
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ContextConfiguration(classes = EmptyDatabaseConfig.class)
@DirtiesContext
public class TransactionalAfterTestMethodSqlScriptsTests extends AbstractTransactionalJUnit4SpringContextTests {

	@Rule
	public TestName testName = new TestName();


	@AfterTransaction
	public void afterTransaction() {
		if ("test01".equals(testName.getMethodName())) {
			try {
				assertNumUsers(99);
				fail("Should throw a BadSqlGrammarException after test01, assuming 'drop-schema.sql' was executed");
			}
			catch (BadSqlGrammarException e) {
				/* expected */
			}
		}
	}

	@Test
	@SqlGroup({//
	@Sql({ "schema.sql", "data.sql" }),//
		@Sql(scripts = "drop-schema.sql", executionPhase = AFTER_TEST_METHOD) //
	})
	// test## is required for @FixMethodOrder.
	public void test01() {
		assertNumUsers(1);
	}

	@Test
	@Sql({ "schema.sql", "data.sql", "data-add-dogbert.sql" })
	// test## is required for @FixMethodOrder.
	public void test02() {
		assertNumUsers(2);
	}

	protected void assertNumUsers(int expected) {
		assertEquals("Number of rows in the 'user' table.", expected, countRowsInTable("user"));
	}

}
