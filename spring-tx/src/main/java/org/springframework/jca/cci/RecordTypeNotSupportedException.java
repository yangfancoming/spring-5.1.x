

package org.springframework.jca.cci;

import javax.resource.ResourceException;

import org.springframework.dao.InvalidDataAccessResourceUsageException;

/**
 * Exception thrown when the creating of a CCI Record failed because
 * the connector doesn't support the desired CCI Record type.
 *

 * @since 1.2
 */
@SuppressWarnings("serial")
public class RecordTypeNotSupportedException extends InvalidDataAccessResourceUsageException {

	/**
	 * Constructor for RecordTypeNotSupportedException.
	 * @param msg message
	 * @param ex the root ResourceException cause
	 */
	public RecordTypeNotSupportedException(String msg, ResourceException ex) {
		super(msg, ex);
	}

}
