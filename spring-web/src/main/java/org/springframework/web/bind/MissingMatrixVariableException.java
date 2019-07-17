

package org.springframework.web.bind;

import org.springframework.core.MethodParameter;

/**
 * {@link ServletRequestBindingException} subclass that indicates that a matrix
 * variable expected in the method parameters of an {@code @RequestMapping}
 * method is not present among the matrix variables extracted from the URL.
 *

 * @since 5.1
 * @see MissingPathVariableException
 */
@SuppressWarnings("serial")
public class MissingMatrixVariableException extends ServletRequestBindingException {

	private final String variableName;

	private final MethodParameter parameter;


	/**
	 * Constructor for MissingMatrixVariableException.
	 * @param variableName the name of the missing matrix variable
	 * @param parameter the method parameter
	 */
	public MissingMatrixVariableException(String variableName, MethodParameter parameter) {
		super("");
		this.variableName = variableName;
		this.parameter = parameter;
	}


	@Override
	public String getMessage() {
		return "Missing matrix variable '" + this.variableName +
				"' for method parameter of type " + this.parameter.getNestedParameterType().getSimpleName();
	}

	/**
	 * Return the expected name of the matrix variable.
	 */
	public final String getVariableName() {
		return this.variableName;
	}

	/**
	 * Return the method parameter bound to the matrix variable.
	 */
	public final MethodParameter getParameter() {
		return this.parameter;
	}

}
