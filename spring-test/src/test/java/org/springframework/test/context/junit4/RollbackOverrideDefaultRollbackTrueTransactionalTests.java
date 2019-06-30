

package org.springframework.test.context.junit4;

import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;

import static org.junit.Assert.*;
import static org.springframework.test.transaction.TransactionTestUtils.*;

/**
 * Extension of {@link DefaultRollbackTrueRollbackAnnotationTransactionalTests}
 * which tests method-level <em>rollback override</em> behavior via the
 * {@link Rollback @Rollback} annotation.
 *
 * @author Sam Brannen
 * @since 2.5
 * @see Rollback
 */
public class RollbackOverrideDefaultRollbackTrueTransactionalTests
		extends DefaultRollbackTrueRollbackAnnotationTransactionalTests {

	private static JdbcTemplate jdbcTemplate;


	@Autowired
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Before
	@Override
	public void verifyInitialTestData() {
		clearPersonTable(jdbcTemplate);
		assertEquals("Adding bob", 1, addPerson(jdbcTemplate, BOB));
		assertEquals("Verifying the initial number of rows in the person table.", 1,
			countRowsInPersonTable(jdbcTemplate));
	}

	@Test
	@Rollback(false)
	@Override
	public void modifyTestDataWithinTransaction() {
		assertInTransaction(true);
		assertEquals("Adding jane", 1, addPerson(jdbcTemplate, JANE));
		assertEquals("Adding sue", 1, addPerson(jdbcTemplate, SUE));
		assertEquals("Verifying the number of rows in the person table within a transaction.", 3,
			countRowsInPersonTable(jdbcTemplate));
	}

	@AfterClass
	public static void verifyFinalTestData() {
		assertEquals("Verifying the final number of rows in the person table after all tests.", 3,
			countRowsInPersonTable(jdbcTemplate));
	}

}
