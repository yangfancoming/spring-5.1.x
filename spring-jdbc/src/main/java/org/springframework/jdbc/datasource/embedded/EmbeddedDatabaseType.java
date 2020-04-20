

package org.springframework.jdbc.datasource.embedded;

/**
 * A supported embedded database type.
 * @since 3.0
 */
public enum EmbeddedDatabaseType {

	HSQL, /** The <a href="http://hsqldb.org">Hypersonic</a> Embedded Java SQL Database. */
	H2, /** The <a href="https://h2database.com">H2</a> Embedded Java SQL Database Engine. */
	DERBY /** The <a href="https://db.apache.org/derby">Apache Derby</a> Embedded SQL Database. */
}
