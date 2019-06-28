

package org.springframework.aop.support;

import org.springframework.aop.Pointcut;
import org.springframework.lang.Nullable;

/**
 * Interface to be implemented by pointcuts that use String expressions.
 *
 * @author Rob Harrop
 * @since 2.0
 */
public interface ExpressionPointcut extends Pointcut {

	/**
	 * Return the String expression for this pointcut.
	 */
	@Nullable
	String getExpression();

}
