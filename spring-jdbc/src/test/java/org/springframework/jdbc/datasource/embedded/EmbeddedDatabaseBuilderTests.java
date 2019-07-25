

package org.springframework.jdbc.datasource.embedded;

import org.junit.Test;
import org.springframework.core.io.ClassRelativeResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.CannotReadScriptException;
import org.springframework.jdbc.datasource.init.ScriptStatementFailedException;

import static org.junit.Assert.*;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.*;

/**
 * Integration tests for {@link EmbeddedDatabaseBuilder}.
 */
public class EmbeddedDatabaseBuilderTests {

	private final EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder(new ClassRelativeResourceLoader(getClass()));

	@Test
	public void addDefaultScripts() {
		doTwice(()->{
			EmbeddedDatabase db = new EmbeddedDatabaseBuilder()//
			.addDefaultScripts()//
			.build();
			assertDatabaseCreatedAndShutdown(db);
		});
	}

	@Test(expected = CannotReadScriptException.class)
	public void addScriptWithBogusFileName() {
		new EmbeddedDatabaseBuilder().addScript("bogus.sql").build();
	}

	@Test
	public void addScript() {
		doTwice(()->{
			EmbeddedDatabase db = builder//
			.addScript("db-schema.sql")//
			.addScript("db-test-data.sql")//
			.build();
			assertDatabaseCreatedAndShutdown(db);
		});
	}

	@Test
	public void addScripts() {
		doTwice(()->{
			EmbeddedDatabase db = builder//
			.addScripts("db-schema.sql", "db-test-data.sql")//
			.build();
			assertDatabaseCreatedAndShutdown(db);
		});
	}

	@Test
	public void addScriptsWithDefaultCommentPrefix() {
		doTwice(()->{
			EmbeddedDatabase db = builder//
			.addScripts("db-schema-comments.sql", "db-test-data.sql")//
			.build();
			assertDatabaseCreatedAndShutdown(db);
		});
	}

	@Test
	public void addScriptsWithCustomCommentPrefix() {
		doTwice(()->{
			EmbeddedDatabase db = builder//
			.addScripts("db-schema-custom-comments.sql", "db-test-data.sql")//
			.setCommentPrefix("~")//
			.build();
			assertDatabaseCreatedAndShutdown(db);
		});
	}

	@Test
	public void addScriptsWithCustomBlockComments() {
		doTwice(()->{
			EmbeddedDatabase db = builder//
			.addScripts("db-schema-block-comments.sql", "db-test-data.sql")//
			.setBlockCommentStartDelimiter("{*")//
			.setBlockCommentEndDelimiter("*}")//
			.build();
			assertDatabaseCreatedAndShutdown(db);
		});
	}

	@Test
	public void setTypeToH2() {
		doTwice(()->{
			EmbeddedDatabase db = builder//
			.setType(H2)//
			.addScripts("db-schema.sql", "db-test-data.sql")//
			.build();
			assertDatabaseCreatedAndShutdown(db);
		});
	}

	@Test
	public void setTypeToDerbyAndIgnoreFailedDrops() {
		doTwice(()->{
			EmbeddedDatabase db = builder//
			.setType(DERBY)//
			.ignoreFailedDrops(true)//
			.addScripts("db-schema-derby-with-drop.sql", "db-test-data.sql").build();
			assertDatabaseCreatedAndShutdown(db);
		});
	}

	@Test
	public void createSameSchemaTwiceWithoutUniqueDbNames() {
		EmbeddedDatabase db1 = new EmbeddedDatabaseBuilder(new ClassRelativeResourceLoader(getClass()))//
		.addScripts("db-schema-without-dropping.sql").build();

		try {
			new EmbeddedDatabaseBuilder(new ClassRelativeResourceLoader(getClass()))//
			.addScripts("db-schema-without-dropping.sql").build();

			fail("Should have thrown a ScriptStatementFailedException");
		}
		catch (ScriptStatementFailedException e) {
			// expected
		}
		finally {
			db1.shutdown();
		}
	}

	@Test
	public void createSameSchemaTwiceWithGeneratedUniqueDbNames() {
		EmbeddedDatabase db1 = new EmbeddedDatabaseBuilder(new ClassRelativeResourceLoader(getClass()))//
		.addScripts("db-schema-without-dropping.sql", "db-test-data.sql")//
		.generateUniqueName(true)//
		.build();

		JdbcTemplate template1 = new JdbcTemplate(db1);
		assertNumRowsInTestTable(template1, 1);
		template1.update("insert into T_TEST (NAME) values ('Sam')");
		assertNumRowsInTestTable(template1, 2);

		EmbeddedDatabase db2 = new EmbeddedDatabaseBuilder(new ClassRelativeResourceLoader(getClass()))//
		.addScripts("db-schema-without-dropping.sql", "db-test-data.sql")//
		.generateUniqueName(true)//
		.build();
		assertDatabaseCreated(db2);

		db1.shutdown();
		db2.shutdown();
	}

	private void doTwice(Runnable test) {
		test.run();
		test.run();
	}

	private void assertNumRowsInTestTable(JdbcTemplate template, int count) {
		assertEquals(count, template.queryForObject("select count(*) from T_TEST", Integer.class).intValue());
	}

	private void assertDatabaseCreated(EmbeddedDatabase db) {
		assertNumRowsInTestTable(new JdbcTemplate(db), 1);
	}

	private void assertDatabaseCreatedAndShutdown(EmbeddedDatabase db) {
		assertDatabaseCreated(db);
		db.shutdown();
	}

}
