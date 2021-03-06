

package org.springframework.web.bind;

import org.springframework.core.MethodParameter;

/**
 * {@link ServletRequestBindingException} subclass that indicates that a path
 * variable expected in the method parameters of an {@code @RequestMapping}
 * method is not present among the URI variables extracted from the URL.
 * Typically that means the URI template does not match the path variable name
 * declared on the method parameter.
 *
 *
 * @since 4.2
 * @see MissingMatrixVariableException
 */
@SuppressWarnings("serial")
public class MissingPathVariableException extends ServletRequestBindingException {

	private final String variableName;

	private final MethodParameter parameter;


	/**
	 * Constructor for MissingPathVariableException.
	 * @param variableName the name of the missing path variable
	 * @param parameter the method parameter
	 */
	public MissingPathVariableException(String variableName, MethodParameter parameter) {
		super("");
		this.variableName = variableName;
		this.parameter = parameter;
	}


	@Override
	public String getMessage() {
		return "Missing URI template variable '" + this.variableName +
				"' for method parameter of type " + this.parameter.getNestedParameterType().getSimpleName();
	}

	/**
	 * Return the expected name of the path variable.
	 */
	public final String getVariableName() {
		return this.variableName;
	}

	/**
	 * Return the method parameter bound to the path variable.
	 */
	public final MethodParameter getParameter() {
		return this.parameter;
	}

}
