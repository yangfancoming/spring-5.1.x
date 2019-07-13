

package org.springframework.jdbc.core;

import java.sql.Types;

/**
 * Represents a returned update count from a stored procedure call.
 *
 * <p>Returned update counts - like all stored procedure
 * parameters - <b>must</b> have names.
 *
 * @author Thomas Risberg
 */
public class SqlReturnUpdateCount extends SqlParameter {

	/**
	 * Create a new SqlReturnUpdateCount.
	 * @param name name of the parameter, as used in input and output maps
	 */
	public SqlReturnUpdateCount(String name) {
		super(name, Types.INTEGER);
	}


	/**
	 * This implementation always returns {@code false}.
	 */
	@Override
	public boolean isInputValueProvided() {
		return false;
	}

	/**
	 * This implementation always returns {@code true}.
	 */
	@Override
	public boolean isResultsParameter() {
		return true;
	}

}
