

package org.springframework.jdbc.datasource.lookup;

import javax.sql.DataSource;

/**
 * Strategy interface for looking up DataSources by name.
 * <p>Used, for example, to resolve data source names in JPA
 * {@code persistence.xml} files.
 * @since 2.0
 * @see org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager#setDataSourceLookup
 */
@FunctionalInterface
public interface DataSourceLookup {

	/**
	 * Retrieve the DataSource identified by the given name.
	 * @param dataSourceName the name of the DataSource
	 * @return the DataSource (never {@code null})
	 * @throws DataSourceLookupFailureException if the lookup failed
	 */
	DataSource getDataSource(String dataSourceName) throws DataSourceLookupFailureException;

}
