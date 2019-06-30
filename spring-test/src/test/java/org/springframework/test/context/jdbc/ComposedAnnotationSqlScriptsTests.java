

package org.springframework.test.context.jdbc;

import java.lang.annotation.Retention;

import org.junit.Test;

import org.springframework.core.annotation.AliasFor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import static java.lang.annotation.RetentionPolicy.*;
import static org.junit.Assert.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.*;

/**
 * Integration tests that verify support for using {@link Sql @Sql} as a
 * merged, composed annotation.
 *
 * @author Sam Brannen
 * @since 4.3
 */
@ContextConfiguration(classes = EmptyDatabaseConfig.class)
@DirtiesContext
public class ComposedAnnotationSqlScriptsTests extends AbstractTransactionalJUnit4SpringContextTests {

	@Test
	@ComposedSql(
		scripts = { "drop-schema.sql", "schema.sql" },
		statements = "INSERT INTO user VALUES('Dilbert')",
		executionPhase = BEFORE_TEST_METHOD
	)
	public void composedSqlAnnotation() {
		assertEquals("Number of rows in the 'user' table.", 1, countRowsInTable("user"));
	}


	@Sql
	@Retention(RUNTIME)
	@interface ComposedSql {

		@AliasFor(annotation = Sql.class)
		String[] value() default {};

		@AliasFor(annotation = Sql.class)
		String[] scripts() default {};

		@AliasFor(annotation = Sql.class)
		String[] statements() default {};

		@AliasFor(annotation = Sql.class)
		ExecutionPhase executionPhase();
	}

}
