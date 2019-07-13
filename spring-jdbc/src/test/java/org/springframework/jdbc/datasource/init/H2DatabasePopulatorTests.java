

package org.springframework.jdbc.datasource.init;

import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * @author Sam Brannen
 * @since 4.0.3
 */
public class H2DatabasePopulatorTests extends AbstractDatabasePopulatorTests {

	protected EmbeddedDatabaseType getEmbeddedDatabaseType() {
		return EmbeddedDatabaseType.H2;
	}

	/**
	 * https://jira.spring.io/browse/SPR-15896
	 *
	 * @since 5.0
	 */
	@Test
	public void scriptWithH2Alias() throws Exception {
		databasePopulator.addScript(usersSchema());
		databasePopulator.addScript(resource("db-test-data-h2-alias.sql"));
		// Set statement separator to double newline so that ";" is not
		// considered a statement separator within the source code of the
		// aliased function 'REVERSE'.
		databasePopulator.setSeparator("\n\n");
		DatabasePopulatorUtils.execute(databasePopulator, db);
		String sql = "select REVERSE(first_name) from users where last_name='Brannen'";
		assertThat(jdbcTemplate.queryForObject(sql, String.class), equalTo("maS"));
	}

}
