

package org.springframework.jdbc.datasource.init;

import org.springframework.dao.DataAccessException;
import org.springframework.lang.Nullable;

/**
 * Root of the hierarchy of data access exceptions that are related to processing
 * of SQL scripts.
 *
 * @author Sam Brannen
 * @since 4.0.3
 */
@SuppressWarnings("serial")
public abstract class ScriptException extends DataAccessException {

	/**
	 * Constructor for {@code ScriptException}.
	 * @param message the detail message
	 */
	public ScriptException(String message) {
		super(message);
	}

	/**
	 * Constructor for {@code ScriptException}.
	 * @param message the detail message
	 * @param cause the root cause
	 */
	public ScriptException(String message, @Nullable Throwable cause) {
		super(message, cause);
	}

}
