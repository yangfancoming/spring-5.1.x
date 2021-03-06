

package org.springframework.jdbc.core;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An interface used by {@link JdbcTemplate} for processing rows of a
 * {@link java.sql.ResultSet} on a per-row basis. Implementations of
 * this interface perform the actual work of processing each row
 * but don't need to worry about exception handling.
 * {@link java.sql.SQLException SQLExceptions} will be caught and handled
 * by the calling JdbcTemplate.
 *
 * In contrast to a {@link ResultSetExtractor}, a RowCallbackHandler
 * object is typically stateful: It keeps the result state within the
 * object, to be available for later inspection. See
 * {@link RowCountCallbackHandler} for a usage example.
 *
 * Consider using a {@link RowMapper} instead if you need to map
 * exactly one result object per row, assembling them into a List.
 * @see JdbcTemplate
 * @see RowMapper
 * @see ResultSetExtractor
 * @see RowCountCallbackHandler
 */
@FunctionalInterface
public interface RowCallbackHandler {

	/**
	 * Implementations must implement this method to process each row of data
	 * in the ResultSet. This method should not call {@code next()} on
	 * the ResultSet; it is only supposed to extract values of the current row.
	 * Exactly what the implementation chooses to do is up to it:
	 * A trivial implementation might simply count rows, while another
	 * implementation might build an XML document.
	 * @param rs the ResultSet to process (pre-initialized for the current row)
	 * @throws SQLException if a SQLException is encountered getting
	 * column values (that is, there's no need to catch SQLException)
	 */
	void processRow(ResultSet rs) throws SQLException;

}
