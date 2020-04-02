

package org.springframework.jdbc.support.incrementer;

import javax.sql.DataSource;

/**
 * {@link DataFieldMaxValueIncrementer} that retrieves the next value
 * of a given PostgreSQL sequence.
 *
 * Thanks to Tomislav Urban for the suggestion!
 *

 * @deprecated in favor of the differently named {@link PostgresSequenceMaxValueIncrementer}
 */
@Deprecated
public class PostgreSQLSequenceMaxValueIncrementer extends PostgresSequenceMaxValueIncrementer {

	/**
	 * Default constructor for bean property style usage.
	 * @see #setDataSource
	 * @see #setIncrementerName
	 */
	public PostgreSQLSequenceMaxValueIncrementer() {
	}

	/**
	 * Convenience constructor.
	 * @param dataSource the DataSource to use
	 * @param incrementerName the name of the sequence/table to use
	 */
	public PostgreSQLSequenceMaxValueIncrementer(DataSource dataSource, String incrementerName) {
		super(dataSource, incrementerName);
	}

}
