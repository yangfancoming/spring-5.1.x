

package org.springframework.test.context.jdbc;

import org.junit.Test;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import static org.junit.Assert.*;

/**
 * Modified copy of {@link CustomScriptSyntaxSqlScriptsTests} with
 * {@link SqlConfig @SqlConfig} defined at the class level.
 *
 * @author Sam Brannen
 * @since 4.1
 */
@ContextConfiguration(classes = EmptyDatabaseConfig.class)
@DirtiesContext
@SqlConfig(commentPrefix = "`", blockCommentStartDelimiter = "#$", blockCommentEndDelimiter = "$#", separator = "@@")
public class GlobalCustomScriptSyntaxSqlScriptsTests extends AbstractTransactionalJUnit4SpringContextTests {

	@Test
	@Sql(scripts = "schema.sql", config = @SqlConfig(separator = ";"))
	@Sql("data-add-users-with-custom-script-syntax.sql")
	public void methodLevelScripts() {
		assertNumUsers(3);
	}

	protected void assertNumUsers(int expected) {
		assertEquals("Number of rows in the 'user' table.", expected, countRowsInTable("user"));
	}

}
