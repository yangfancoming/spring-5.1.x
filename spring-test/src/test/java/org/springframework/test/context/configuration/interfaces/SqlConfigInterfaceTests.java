

package org.springframework.test.context.configuration.interfaces;

import org.junit.Test;

import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import static org.junit.Assert.*;

/**
 * @author Sam Brannen
 * @since 4.3
 */
public class SqlConfigInterfaceTests extends AbstractTransactionalJUnit4SpringContextTests
		implements SqlConfigTestInterface {

	@Test
	@Sql(scripts = "/org/springframework/test/context/jdbc/schema.sql", //
			config = @SqlConfig(separator = ";"))
	@Sql("/org/springframework/test/context/jdbc/data-add-users-with-custom-script-syntax.sql")
	public void methodLevelScripts() {
		assertNumUsers(3);
	}

	protected void assertNumUsers(int expected) {
		assertEquals("Number of rows in the 'user' table.", expected, countRowsInTable("user"));
	}

}
