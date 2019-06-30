

package org.springframework.test.context.junit4;

import org.junit.runner.RunWith;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Abstract base class for verifying support of Spring's {@link Transactional
 * &#64;Transactional} annotation.
 *
 * @author Sam Brannen
 * @since 2.5
 * @see ClassLevelTransactionalSpringRunnerTests
 * @see MethodLevelTransactionalSpringRunnerTests
 * @see Transactional
 */
@RunWith(SpringRunner.class)
@ContextConfiguration("transactionalTests-context.xml")
public abstract class AbstractTransactionalSpringRunnerTests {

	protected static final String BOB = "bob";
	protected static final String JANE = "jane";
	protected static final String SUE = "sue";
	protected static final String LUKE = "luke";
	protected static final String LEIA = "leia";
	protected static final String YODA = "yoda";


	protected static int clearPersonTable(JdbcTemplate jdbcTemplate) {
		return jdbcTemplate.update("DELETE FROM person");
	}

	protected static int countRowsInPersonTable(JdbcTemplate jdbcTemplate) {
		return jdbcTemplate.queryForObject("SELECT COUNT(0) FROM person", Integer.class);
	}

	protected static int addPerson(JdbcTemplate jdbcTemplate, String name) {
		return jdbcTemplate.update("INSERT INTO person VALUES(?)", name);
	}

	protected static int deletePerson(JdbcTemplate jdbcTemplate, String name) {
		return jdbcTemplate.update("DELETE FROM person WHERE name=?", name);
	}

}
