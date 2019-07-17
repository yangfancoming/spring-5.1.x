

package org.springframework.jdbc.datasource.embedded;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class for {@link EmbeddedDatabaseConfigurer} implementations
 * providing common shutdown behavior through a "SHUTDOWN" statement.


 * @since 3.0
 */
abstract class AbstractEmbeddedDatabaseConfigurer implements EmbeddedDatabaseConfigurer {

	protected final Log logger = LogFactory.getLog(getClass());


	@Override
	public void shutdown(DataSource dataSource, String databaseName) {
		Connection con = null;
		try {
			con = dataSource.getConnection();
			if (con != null) {
				try (Statement stmt = con.createStatement()) {
					stmt.execute("SHUTDOWN");
				}
			}
		}
		catch (SQLException ex) {
			logger.info("Could not shut down embedded database", ex);
		}
		finally {
			if (con != null) {
				try {
					con.close();
				}
				catch (Throwable ex) {
					logger.debug("Could not close JDBC Connection on shutdown", ex);
				}
			}
		}
	}

}
