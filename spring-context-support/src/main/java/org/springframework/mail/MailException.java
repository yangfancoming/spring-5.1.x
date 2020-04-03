

package org.springframework.mail;

import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;

/**
 * Base class for all mail exceptions.
 *
 * @author Dmitriy Kopylenko
 */
@SuppressWarnings("serial")
public abstract class MailException extends NestedRuntimeException {

	/**
	 * Constructor for MailException.
	 * @param msg the detail message
	 */
	public MailException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for MailException.
	 * @param msg the detail message
	 * @param cause the root cause from the mail API in use
	 */
	public MailException(@Nullable String msg, @Nullable Throwable cause) {
		super(msg, cause);
	}

}
