

package org.springframework.aop.support;

import java.io.Serializable;

import org.springframework.lang.Nullable;

/**
 * Abstract superclass for expression pointcuts,offering location and expression properties.
 * @since 2.0
 * @see #setLocation
 * @see #setExpression
 */
@SuppressWarnings("serial")
public abstract class AbstractExpressionPointcut implements ExpressionPointcut, Serializable {

	@Nullable
	private String location;

	@Nullable
	private String expression;

	/**
	 * Set the location for debugging.
	 */
	public void setLocation(@Nullable String location) {
		this.location = location;
	}

	/**
	 * Return location information about the pointcut expression  if available. This is useful in debugging.
	 * @return location information as a human-readable String, or {@code null} if none is available
	 */
	@Nullable
	public String getLocation() {
		return this.location;
	}

	public void setExpression(@Nullable String expression) {
		this.expression = expression;
		try {
			onSetExpression(expression);
		}catch (IllegalArgumentException ex) {
			// Fill in location information if possible.
			if (this.location != null) {
				throw new IllegalArgumentException("Invalid expression at location [" + this.location + "]: " + ex);
			}else {
				throw ex;
			}
		}
	}

	/**
	 * Called when a new pointcut expression is set.
	 * The expression should be parsed at this point if possible.
	 * This implementation is empty.
	 * @param expression expression to set
	 * @throws IllegalArgumentException if the expression is invalid
	 * @see #setExpression
	 */
	protected void onSetExpression(@Nullable String expression) throws IllegalArgumentException {
	}

	/**
	 * Return this pointcut's expression.
	 */
	@Override
	@Nullable
	public String getExpression() {
		return this.expression;
	}

}
