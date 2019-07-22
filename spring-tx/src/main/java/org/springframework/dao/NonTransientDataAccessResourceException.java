

package org.springframework.dao;

import org.springframework.lang.Nullable;

/**
 * Data access exception thrown when a resource fails completely and the failure is permanent.
 *
 * @author Thomas Risberg
 * @since 2.5
 * @see java.sql.SQLNonTransientConnectionException
 */
@SuppressWarnings("serial")
public class NonTransientDataAccessResourceException extends NonTransientDataAccessException {

	/**
	 * Constructor for NonTransientDataAccessResourceException.
	 * @param msg the detail message
	 */
	public NonTransientDataAccessResourceException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for NonTransientDataAccessResourceException.
	 * @param msg the detail message
	 * @param cause the root cause from the data access API in use
	 */
	public NonTransientDataAccessResourceException(String msg, @Nullable Throwable cause) {
		super(msg, cause);
	}

}
