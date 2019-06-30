

package org.springframework.test.context.jdbc;

import javax.sql.DataSource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * Transactional integration tests for {@link Sql @Sql} support with
 * inlined SQL {@link Sql#statements statements}.
 *
 * @author Sam Brannen
 * @since 4.2
 * @see TransactionalSqlScriptsTests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ContextConfiguration(classes = EmptyDatabaseConfig.class)
@Transactional
@Sql(
	scripts    = "schema.sql",
	statements = "INSERT INTO user VALUES('Dilbert')"
)
@DirtiesContext
public class TransactionalInlinedStatementsSqlScriptsTests {

	protected JdbcTemplate jdbcTemplate;


	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Test
	// test##_ prefix is required for @FixMethodOrder.
	public void test01_classLevelScripts() {
		assertNumUsers(1);
	}

	@Test
	@Sql(statements = "DROP TABLE user IF EXISTS")
	@Sql("schema.sql")
	@Sql(statements = "INSERT INTO user VALUES ('Dilbert'), ('Dogbert'), ('Catbert')")
	// test##_ prefix is required for @FixMethodOrder.
	public void test02_methodLevelScripts() {
		assertNumUsers(3);
	}

	protected int countRowsInTable(String tableName) {
		return JdbcTestUtils.countRowsInTable(this.jdbcTemplate, tableName);
	}

	protected void assertNumUsers(int expected) {
		assertEquals("Number of rows in the 'user' table.", expected, countRowsInTable("user"));
	}

}
