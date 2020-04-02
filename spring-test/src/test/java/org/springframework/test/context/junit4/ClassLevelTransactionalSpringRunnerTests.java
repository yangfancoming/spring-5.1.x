

package org.springframework.test.context.junit4;

import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;
import static org.springframework.test.transaction.TransactionTestUtils.*;

/**
 * JUnit 4 based integration test which verifies support of Spring's
 * {@link Transactional &#64;Transactional}, {@link TestExecutionListeners
 * &#64;TestExecutionListeners}, and {@link ContextConfiguration
 * &#64;ContextConfiguration} annotations in conjunction with the
 * {@link SpringRunner} and the following
 * {@link TestExecutionListener TestExecutionListeners}:
 *
 * <ul>
 * <li>{@link DependencyInjectionTestExecutionListener}</li>
 * <li>{@link DirtiesContextTestExecutionListener}</li>
 * <li>{@link TransactionalTestExecutionListener}</li>
 * </ul>
 *
 * This class specifically tests usage of {@code @Transactional} defined
 * at the <strong>class level</strong>.
 *
 * @author Sam Brannen
 * @since 2.5
 * @see MethodLevelTransactionalSpringRunnerTests
 */
@Transactional
public class ClassLevelTransactionalSpringRunnerTests extends AbstractTransactionalSpringRunnerTests {

	protected static JdbcTemplate jdbcTemplate;


	@Autowired
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@AfterClass
	public static void verifyFinalTestData() {
		assertEquals("Verifying the final number of rows in the person table after all tests.", 4,
			countRowsInPersonTable(jdbcTemplate));
	}

	@Before
	public void verifyInitialTestData() {
		clearPersonTable(jdbcTemplate);
		assertEquals("Adding bob", 1, addPerson(jdbcTemplate, BOB));
		assertEquals("Verifying the initial number of rows in the person table.", 1,
			countRowsInPersonTable(jdbcTemplate));
	}

	@Test
	public void modifyTestDataWithinTransaction() {
		assertInTransaction(true);
		assertEquals("Deleting bob", 1, deletePerson(jdbcTemplate, BOB));
		assertEquals("Adding jane", 1, addPerson(jdbcTemplate, JANE));
		assertEquals("Adding sue", 1, addPerson(jdbcTemplate, SUE));
		assertEquals("Verifying the number of rows in the person table within a transaction.", 2,
			countRowsInPersonTable(jdbcTemplate));
	}

	@Test
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void modifyTestDataWithoutTransaction() {
		assertInTransaction(false);
		assertEquals("Adding luke", 1, addPerson(jdbcTemplate, LUKE));
		assertEquals("Adding leia", 1, addPerson(jdbcTemplate, LEIA));
		assertEquals("Adding yoda", 1, addPerson(jdbcTemplate, YODA));
		assertEquals("Verifying the number of rows in the person table without a transaction.", 4,
			countRowsInPersonTable(jdbcTemplate));
	}

}
