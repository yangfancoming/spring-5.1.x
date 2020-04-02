

package org.springframework.jdbc.datasource.init;

import javax.sql.DataSource;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Used to {@linkplain #setDatabasePopulator set up} a database during
 * initialization and {@link #setDatabaseCleaner clean up} a database during
 * destruction.
 *
 * @author Dave Syer
 * @author Sam Brannen
 * @since 3.0
 * @see DatabasePopulator
 */
public class DataSourceInitializer implements InitializingBean, DisposableBean {

	@Nullable
	private DataSource dataSource;

	@Nullable
	private DatabasePopulator databasePopulator;

	@Nullable
	private DatabasePopulator databaseCleaner;

	private boolean enabled = true;


	/**
	 * The {@link DataSource} for the database to populate when this component
	 * is initialized and to clean up when this component is shut down.
	 * This property is mandatory with no default provided.
	 * @param dataSource the DataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Set the {@link DatabasePopulator} to execute during the bean initialization phase.
	 * @param databasePopulator the {@code DatabasePopulator} to use during initialization
	 * @see #setDatabaseCleaner
	 */
	public void setDatabasePopulator(DatabasePopulator databasePopulator) {
		this.databasePopulator = databasePopulator;
	}

	/**
	 * Set the {@link DatabasePopulator} to execute during the bean destruction
	 * phase, cleaning up the database and leaving it in a known state for others.
	 * @param databaseCleaner the {@code DatabasePopulator} to use during destruction
	 * @see #setDatabasePopulator
	 */
	public void setDatabaseCleaner(DatabasePopulator databaseCleaner) {
		this.databaseCleaner = databaseCleaner;
	}

	/**
	 * Flag to explicitly enable or disable the {@linkplain #setDatabasePopulator
	 * database populator} and {@linkplain #setDatabaseCleaner database cleaner}.
	 * @param enabled {@code true} if the database populator and database cleaner
	 * should be called on startup and shutdown, respectively
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}


	/**
	 * Use the {@linkplain #setDatabasePopulator database populator} to set up
	 * the database.
	 */
	@Override
	public void afterPropertiesSet() {
		execute(this.databasePopulator);
	}

	/**
	 * Use the {@linkplain #setDatabaseCleaner database cleaner} to clean up the
	 * database.
	 */
	@Override
	public void destroy() {
		execute(this.databaseCleaner);
	}

	private void execute(@Nullable DatabasePopulator populator) {
		Assert.state(this.dataSource != null, "DataSource must be set");
		if (this.enabled && populator != null) {
			DatabasePopulatorUtils.execute(populator, this.dataSource);
		}
	}

}
