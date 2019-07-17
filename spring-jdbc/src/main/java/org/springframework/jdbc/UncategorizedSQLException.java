

package org.springframework.jdbc;

import java.sql.SQLException;

import org.springframework.dao.UncategorizedDataAccessException;
import org.springframework.lang.Nullable;

/**
 * Exception thrown when we can't classify a SQLException into
 * one of our generic data access exceptions.
 *
 * @author Rod Johnson

 */
@SuppressWarnings("serial")
public class UncategorizedSQLException extends UncategorizedDataAccessException {

	/** SQL that led to the problem. */
	@Nullable
	private final String sql;


	/**
	 * Constructor for UncategorizedSQLException.
	 * @param task name of current task
	 * @param sql the offending SQL statement
	 * @param ex the root cause
	 */
	public UncategorizedSQLException(String task, @Nullable String sql, SQLException ex) {
		super(task + "; uncategorized SQLException" + (sql != null ? " for SQL [" + sql + "]" : "") +
				"; SQL state [" + ex.getSQLState() + "]; error code [" + ex.getErrorCode() + "]; " +
				ex.getMessage(), ex);
		this.sql = sql;
	}


	/**
	 * Return the underlying SQLException.
	 */
	public SQLException getSQLException() {
		return (SQLException) getCause();
	}

	/**
	 * Return the SQL that led to the problem (if known).
	 */
	@Nullable
	public String getSql() {
		return this.sql;
	}

}
