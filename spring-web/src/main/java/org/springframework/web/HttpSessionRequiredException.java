

package org.springframework.web;

import javax.servlet.ServletException;

import org.springframework.lang.Nullable;

/**
 * Exception thrown when an HTTP request handler requires a pre-existing session.
 *

 * @since 2.0
 */
@SuppressWarnings("serial")
public class HttpSessionRequiredException extends ServletException {

	@Nullable
	private final String expectedAttribute;


	/**
	 * Create a new HttpSessionRequiredException.
	 * @param msg the detail message
	 */
	public HttpSessionRequiredException(String msg) {
		super(msg);
		this.expectedAttribute = null;
	}

	/**
	 * Create a new HttpSessionRequiredException.
	 * @param msg the detail message
	 * @param expectedAttribute the name of the expected session attribute
	 * @since 4.3
	 */
	public HttpSessionRequiredException(String msg, String expectedAttribute) {
		super(msg);
		this.expectedAttribute = expectedAttribute;
	}


	/**
	 * Return the name of the expected session attribute, if any.
	 * @since 4.3
	 */
	@Nullable
	public String getExpectedAttribute() {
		return this.expectedAttribute;
	}

}
