

package org.springframework.jdbc.datasource.init;

import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * @since 4.0.3
 */
public class HsqlDatabasePopulatorTests extends AbstractDatabasePopulatorTests {

	protected EmbeddedDatabaseType getEmbeddedDatabaseType() {
		return EmbeddedDatabaseType.HSQL;
	}

}
