

package org.springframework.web.bind;

import org.springframework.core.MethodParameter;

/**
 * {@link ServletRequestBindingException} subclass that indicates
 * that a request header expected in the method parameters of an
 * {@code @RequestMapping} method is not present.
 *
 * @author Juergen Hoeller
 * @since 5.1
 * @see MissingRequestCookieException
 */
@SuppressWarnings("serial")
public class MissingRequestHeaderException extends ServletRequestBindingException {

	private final String headerName;

	private final MethodParameter parameter;


	/**
	 * Constructor for MissingRequestHeaderException.
	 * @param headerName the name of the missing request header
	 * @param parameter the method parameter
	 */
	public MissingRequestHeaderException(String headerName, MethodParameter parameter) {
		super("");
		this.headerName = headerName;
		this.parameter = parameter;
	}


	@Override
	public String getMessage() {
		return "Missing request header '" + this.headerName +
				"' for method parameter of type " + this.parameter.getNestedParameterType().getSimpleName();
	}

	/**
	 * Return the expected name of the request header.
	 */
	public final String getHeaderName() {
		return this.headerName;
	}

	/**
	 * Return the method parameter bound to the request header.
	 */
	public final MethodParameter getParameter() {
		return this.parameter;
	}

}
