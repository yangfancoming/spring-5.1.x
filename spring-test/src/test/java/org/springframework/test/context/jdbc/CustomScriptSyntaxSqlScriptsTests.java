

package org.springframework.test.context.jdbc;

import org.junit.Test;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import static org.junit.Assert.*;

/**
 * Integration tests that verify support for custom SQL script syntax
 * configured via {@link SqlConfig @SqlConfig}.
 *
 * @author Sam Brannen
 * @since 4.1
 */
@ContextConfiguration(classes = EmptyDatabaseConfig.class)
@DirtiesContext
public class CustomScriptSyntaxSqlScriptsTests extends AbstractTransactionalJUnit4SpringContextTests {

	@Test
	@Sql("schema.sql")
	@Sql(scripts = "data-add-users-with-custom-script-syntax.sql",//
	config = @SqlConfig(commentPrefix = "`", blockCommentStartDelimiter = "#$", blockCommentEndDelimiter = "$#", separator = "@@"))
	public void methodLevelScripts() {
		assertNumUsers(3);
	}

	protected void assertNumUsers(int expected) {
		assertEquals("Number of rows in the 'user' table.", expected, countRowsInTable("user"));
	}

}
