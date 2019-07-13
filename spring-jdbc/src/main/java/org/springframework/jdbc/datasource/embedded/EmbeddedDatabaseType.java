

package org.springframework.jdbc.datasource.embedded;

/**
 * A supported embedded database type.
 *
 * @author Keith Donald
 * @author Oliver Gierke
 * @since 3.0
 */
public enum EmbeddedDatabaseType {

	/** The <a href="http://hsqldb.org">Hypersonic</a> Embedded Java SQL Database. */
	HSQL,

	/** The <a href="https://h2database.com">H2</a> Embedded Java SQL Database Engine. */
	H2,

	/** The <a href="https://db.apache.org/derby">Apache Derby</a> Embedded SQL Database. */
	DERBY

}
