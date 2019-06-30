

package org.springframework.test.context.jdbc;

import org.junit.Test;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import static org.junit.Assert.*;

/**
 * Integration tests that verify support for default SQL script detection.
 *
 * @author Sam Brannen
 * @since 4.1
 */
@ContextConfiguration(classes = EmptyDatabaseConfig.class)
@Sql
@DirtiesContext
public class DefaultScriptDetectionSqlScriptsTests extends AbstractTransactionalJUnit4SpringContextTests {

	@Test
	public void classLevel() {
		assertNumUsers(2);
	}

	@Test
	@Sql
	public void methodLevel() {
		assertNumUsers(3);
	}

	protected void assertNumUsers(int expected) {
		assertEquals("Number of rows in the 'user' table.", expected, countRowsInTable("user"));
	}

}
