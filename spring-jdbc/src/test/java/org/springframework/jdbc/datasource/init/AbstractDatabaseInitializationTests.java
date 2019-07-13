

package org.springframework.jdbc.datasource.init;

import org.junit.After;
import org.junit.Before;

import org.springframework.core.io.ClassRelativeResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Abstract base class for integration tests involving database initialization.
 *
 * @author Sam Brannen
 * @since 4.0.3
 */
public abstract class AbstractDatabaseInitializationTests {

	private final ClassRelativeResourceLoader resourceLoader = new ClassRelativeResourceLoader(getClass());

	EmbeddedDatabase db;

	JdbcTemplate jdbcTemplate;


	@Before
	public void setUp() {
		db = new EmbeddedDatabaseBuilder().setType(getEmbeddedDatabaseType()).build();
		jdbcTemplate = new JdbcTemplate(db);
	}

	@After
	public void shutDown() {
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.clear();
			TransactionSynchronizationManager.unbindResource(db);
		}
		db.shutdown();
	}

	abstract EmbeddedDatabaseType getEmbeddedDatabaseType();

	Resource resource(String path) {
		return resourceLoader.getResource(path);
	}

	Resource defaultSchema() {
		return resource("db-schema.sql");
	}

	Resource usersSchema() {
		return resource("users-schema.sql");
	}

	void assertUsersDatabaseCreated(String... lastNames) {
		for (String lastName : lastNames) {
			assertThat("Did not find user with last name [" + lastName + "].",
				jdbcTemplate.queryForObject("select count(0) from users where last_name = ?", Integer.class, lastName),
				equalTo(1));
		}
	}

}
