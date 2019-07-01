

package org.springframework.expression;

/**
 * Supported operations that an {@link OperatorOverloader} can implement for any pair of
 * operands.
 *
 * @author Andy Clement
 * @since 3.0
 */
public enum Operation {

	/**
	 * Add operation.
	 */
	ADD,

	/**
	 * Subtract operation.
	 */
	SUBTRACT,

	/**
	 * Divide operation.
	 */
	DIVIDE,

	/**
	 * Multiply operation.
	 */
	MULTIPLY,

	/**
	 * Modulus operation.
	 */
	MODULUS,

	/**
	 * Power operation.
	 */
	POWER

}
