

package org.springframework.dao;

import org.springframework.lang.Nullable;

/**
 * Exception thrown if certain expected data could not be retrieved, e.g.
 * when looking up specific data via a known identifier. This exception
 * will be thrown either by O/R mapping tools or by DAO implementations.
 *

 * @since 13.10.2003
 */
@SuppressWarnings("serial")
public class DataRetrievalFailureException extends NonTransientDataAccessException {

	/**
	 * Constructor for DataRetrievalFailureException.
	 * @param msg the detail message
	 */
	public DataRetrievalFailureException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for DataRetrievalFailureException.
	 * @param msg the detail message
	 * @param cause the root cause from the data access API in use
	 */
	public DataRetrievalFailureException(String msg, @Nullable Throwable cause) {
		super(msg, cause);
	}

}
