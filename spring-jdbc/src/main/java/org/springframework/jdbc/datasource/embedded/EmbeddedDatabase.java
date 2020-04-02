

package org.springframework.jdbc.datasource.embedded;

import javax.sql.DataSource;

/**
 * {@code EmbeddedDatabase} serves as a handle to an embedded database instance.
 *
 * An {@code EmbeddedDatabase} is also a {@link DataSource} and adds a
 * {@link #shutdown} operation so that the embedded database instance can be
 * shut down gracefully.
 *
 * @author Keith Donald
 * @author Sam Brannen
 * @since 3.0
 */
public interface EmbeddedDatabase extends DataSource {

	/**
	 * Shut down this embedded database.
	 */
	void shutdown();

}
