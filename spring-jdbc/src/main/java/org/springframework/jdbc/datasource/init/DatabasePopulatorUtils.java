

package org.springframework.jdbc.datasource.init;

import java.sql.Connection;
import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.util.Assert;

/**
 * Utility methods for executing a {@link DatabasePopulator}.
 *

 * @author Oliver Gierke
 * @author Sam Brannen
 * @since 3.1
 */
public abstract class DatabasePopulatorUtils {

	/**
	 * Execute the given {@link DatabasePopulator} against the given {@link DataSource}.
	 * @param populator the {@code DatabasePopulator} to execute
	 * @param dataSource the {@code DataSource} to execute against
	 * @throws DataAccessException if an error occurs, specifically a {@link ScriptException}
	 */
	public static void execute(DatabasePopulator populator, DataSource dataSource) throws DataAccessException {
		Assert.notNull(populator, "DatabasePopulator must not be null");
		Assert.notNull(dataSource, "DataSource must not be null");
		try {
			Connection connection = DataSourceUtils.getConnection(dataSource);
			try {
				populator.populate(connection);
			}
			finally {
				DataSourceUtils.releaseConnection(connection, dataSource);
			}
		}
		catch (Throwable ex) {
			if (ex instanceof ScriptException) {
				throw (ScriptException) ex;
			}
			throw new UncategorizedScriptException("Failed to execute database script", ex);
		}
	}

}
