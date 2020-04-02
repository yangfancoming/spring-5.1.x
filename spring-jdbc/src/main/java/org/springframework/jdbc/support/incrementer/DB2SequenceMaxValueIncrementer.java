

package org.springframework.jdbc.support.incrementer;

import javax.sql.DataSource;

/**
 * {@link DataFieldMaxValueIncrementer} that retrieves the next value
 * of a given sequence on DB2 LUW (for Linux, Unix and Windows).
 *
 * Thanks to Mark MacMahon for the suggestion!
 *

 * @since 1.1.3
 * @deprecated in favor of the specifically named {@link Db2LuwMaxValueIncrementer}
 */
@Deprecated
public class DB2SequenceMaxValueIncrementer extends Db2LuwMaxValueIncrementer {

	/**
	 * Default constructor for bean property style usage.
	 * @see #setDataSource
	 * @see #setIncrementerName
	 */
	public DB2SequenceMaxValueIncrementer() {
	}

	/**
	 * Convenience constructor.
	 * @param dataSource the DataSource to use
	 * @param incrementerName the name of the sequence/table to use
	 */
	public DB2SequenceMaxValueIncrementer(DataSource dataSource, String incrementerName) {
		super(dataSource, incrementerName);
	}

}
