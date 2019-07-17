

package org.springframework.jdbc;

import java.sql.SQLException;

import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.lang.Nullable;

/**
 * Exception thrown when a ResultSet has been accessed in an invalid fashion.
 * Such exceptions always have a {@code java.sql.SQLException} root cause.
 *
 * <p>This typically happens when an invalid ResultSet column index or name
 * has been specified. Also thrown by disconnected SqlRowSets.
 *

 * @since 1.2
 * @see BadSqlGrammarException
 * @see org.springframework.jdbc.support.rowset.SqlRowSet
 */
@SuppressWarnings("serial")
public class InvalidResultSetAccessException extends InvalidDataAccessResourceUsageException {

	@Nullable
	private final String sql;


	/**
	 * Constructor for InvalidResultSetAccessException.
	 * @param task name of current task
	 * @param sql the offending SQL statement
	 * @param ex the root cause
	 */
	public InvalidResultSetAccessException(String task, String sql, SQLException ex) {
		super(task + "; invalid ResultSet access for SQL [" + sql + "]", ex);
		this.sql = sql;
	}

	/**
	 * Constructor for InvalidResultSetAccessException.
	 * @param ex the root cause
	 */
	public InvalidResultSetAccessException(SQLException ex) {
		super(ex.getMessage(), ex);
		this.sql = null;
	}


	/**
	 * Return the wrapped SQLException.
	 */
	public SQLException getSQLException() {
		return (SQLException) getCause();
	}

	/**
	 * Return the SQL that caused the problem.
	 * @return the offending SQL, if known
	 */
	@Nullable
	public String getSql() {
		return this.sql;
	}

}
