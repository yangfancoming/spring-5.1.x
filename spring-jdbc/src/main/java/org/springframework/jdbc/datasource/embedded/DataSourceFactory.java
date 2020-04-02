

package org.springframework.jdbc.datasource.embedded;

import javax.sql.DataSource;

/**
 * {@code DataSourceFactory} encapsulates the creation of a particular
 * {@link DataSource} implementation such as a
 * {@link org.springframework.jdbc.datasource.SimpleDriverDataSource
 * SimpleDriverDataSource} or a connection pool such as Apache DBCP or C3P0.
 *
 * Call {@link #getConnectionProperties()} to configure normalized
 * {@code DataSource} properties before calling {@link #getDataSource()} to
 * actually get the configured {@code DataSource} instance.
 *
 * @author Keith Donald
 * @author Sam Brannen
 * @since 3.0
 */
public interface DataSourceFactory {

	/**
	 * Get the {@linkplain ConnectionProperties connection properties} of the
	 * {@link #getDataSource DataSource} to be configured.
	 */
	ConnectionProperties getConnectionProperties();

	/**
	 * Get the {@link DataSource} with the {@linkplain #getConnectionProperties
	 * connection properties} applied.
	 */
	DataSource getDataSource();

}
