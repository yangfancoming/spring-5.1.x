

package org.springframework.jdbc.datasource.embedded;

import javax.sql.DataSource;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.lang.Nullable;

/**
 * A subclass of {@link EmbeddedDatabaseFactory} that implements {@link FactoryBean}
 * for registration as a Spring bean. Returns the actual {@link DataSource} that
 * provides connectivity to the embedded database to Spring.
 *
 * <p>The target {@link DataSource} is returned instead of an {@link EmbeddedDatabase}
 * proxy since the {@link FactoryBean} will manage the initialization and destruction
 * lifecycle of the embedded database instance.
 *
 * <p>Implements {@link DisposableBean} to shutdown the embedded database when the
 * managing Spring container is being closed.
 *
 * @author Keith Donald

 * @since 3.0
 */
public class EmbeddedDatabaseFactoryBean extends EmbeddedDatabaseFactory
		implements FactoryBean<DataSource>, InitializingBean, DisposableBean {

	@Nullable
	private DatabasePopulator databaseCleaner;


	/**
	 * Set a script execution to be run in the bean destruction callback,
	 * cleaning up the database and leaving it in a known state for others.
	 * @param databaseCleaner the database script executor to run on destroy
	 * @see #setDatabasePopulator
	 * @see org.springframework.jdbc.datasource.init.DataSourceInitializer#setDatabaseCleaner
	 */
	public void setDatabaseCleaner(DatabasePopulator databaseCleaner) {
		this.databaseCleaner = databaseCleaner;
	}

	@Override
	public void afterPropertiesSet() {
		initDatabase();
	}


	@Override
	@Nullable
	public DataSource getObject() {
		return getDataSource();
	}

	@Override
	public Class<? extends DataSource> getObjectType() {
		return DataSource.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}


	@Override
	public void destroy() {
		if (this.databaseCleaner != null && getDataSource() != null) {
			DatabasePopulatorUtils.execute(this.databaseCleaner, getDataSource());
		}
		shutdownDatabase();
	}

}
