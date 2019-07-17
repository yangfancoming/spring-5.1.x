

package org.springframework.jdbc;

import java.io.IOException;

import org.springframework.dao.DataRetrievalFailureException;

/**
 * Exception to be thrown when a LOB could not be retrieved.
 *

 * @since 1.0.2
 */
@SuppressWarnings("serial")
public class LobRetrievalFailureException extends DataRetrievalFailureException {

	/**
	 * Constructor for LobRetrievalFailureException.
	 * @param msg the detail message
	 */
	public LobRetrievalFailureException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for LobRetrievalFailureException.
	 * @param msg the detail message
	 * @param ex the root cause IOException
	 */
	public LobRetrievalFailureException(String msg, IOException ex) {
		super(msg, ex);
	}

}
