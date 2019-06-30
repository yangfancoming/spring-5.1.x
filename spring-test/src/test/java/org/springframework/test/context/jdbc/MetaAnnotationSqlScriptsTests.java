

package org.springframework.test.context.jdbc;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.Test;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;
import static org.junit.Assert.*;

/**
 * Integration tests that verify support for using {@link Sql @Sql} and
 * {@link SqlGroup @SqlGroup} as meta-annotations.
 *
 * @author Sam Brannen
 * @since 4.1
 */
@ContextConfiguration(classes = EmptyDatabaseConfig.class)
@DirtiesContext
public class MetaAnnotationSqlScriptsTests extends AbstractTransactionalJUnit4SpringContextTests {

	@Test
	@MetaSql
	public void metaSqlAnnotation() {
		assertNumUsers(1);
	}

	@Test
	@MetaSqlGroup
	public void metaSqlGroupAnnotation() {
		assertNumUsers(1);
	}

	protected void assertNumUsers(int expected) {
		assertEquals("Number of rows in the 'user' table.", expected, countRowsInTable("user"));
	}


	@Sql({ "drop-schema.sql", "schema.sql", "data.sql" })
	@Retention(RUNTIME)
	@Target(METHOD)
	static @interface MetaSql {
	}

	@SqlGroup({ @Sql("drop-schema.sql"), @Sql("schema.sql"), @Sql("data.sql") })
	@Retention(RUNTIME)
	@Target(METHOD)
	static @interface MetaSqlGroup {
	}

}
