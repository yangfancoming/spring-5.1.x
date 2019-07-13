

package org.springframework.jdbc.datasource.init;

/**
 * Thrown when we cannot determine anything more specific than "something went
 * wrong while processing an SQL script": for example, a {@link java.sql.SQLException}
 * from JDBC that we cannot pinpoint more precisely.
 *
 * @author Sam Brannen
 * @since 4.0.3
 */
@SuppressWarnings("serial")
public class UncategorizedScriptException extends ScriptException {

	/**
	 * Construct a new {@code UncategorizedScriptException}.
	 * @param message detailed message
	 */
	public UncategorizedScriptException(String message) {
		super(message);
	}

	/**
	 * Construct a new {@code UncategorizedScriptException}.
	 * @param message detailed message
	 * @param cause the root cause
	 */
	public UncategorizedScriptException(String message, Throwable cause) {
		super(message, cause);
	}

}
