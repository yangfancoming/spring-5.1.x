

package org.springframework.web.bind;

import org.springframework.core.MethodParameter;

/**
 * {@link ServletRequestBindingException} subclass that indicates
 * that a request cookie expected in the method parameters of an
 * {@code @RequestMapping} method is not present.
 *

 * @since 5.1
 * @see MissingRequestHeaderException
 */
@SuppressWarnings("serial")
public class MissingRequestCookieException extends ServletRequestBindingException {

	private final String cookieName;

	private final MethodParameter parameter;


	/**
	 * Constructor for MissingRequestCookieException.
	 * @param cookieName the name of the missing request cookie
	 * @param parameter the method parameter
	 */
	public MissingRequestCookieException(String cookieName, MethodParameter parameter) {
		super("");
		this.cookieName = cookieName;
		this.parameter = parameter;
	}


	@Override
	public String getMessage() {
		return "Missing cookie '" + this.cookieName +
				"' for method parameter of type " + this.parameter.getNestedParameterType().getSimpleName();
	}

	/**
	 * Return the expected name of the request cookie.
	 */
	public final String getCookieName() {
		return this.cookieName;
	}

	/**
	 * Return the method parameter bound to the request cookie.
	 */
	public final MethodParameter getParameter() {
		return this.parameter;
	}

}
