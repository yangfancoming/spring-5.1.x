

package org.springframework.aop.support;

import org.aopalliance.aop.Advice;

/**
 * Abstract generic {@link org.springframework.aop.PointcutAdvisor}
 * that allows for any {@link Advice} to be configured.
 *

 * @since 2.0
 * @see #setAdvice
 * @see DefaultPointcutAdvisor
 */
@SuppressWarnings("serial")
public abstract class AbstractGenericPointcutAdvisor extends AbstractPointcutAdvisor {

	private Advice advice = EMPTY_ADVICE;


	/**
	 * Specify the advice that this advisor should apply.
	 */
	public void setAdvice(Advice advice) {
		this.advice = advice;
	}

	@Override
	public Advice getAdvice() {
		return this.advice;
	}


	@Override
	public String toString() {
		return getClass().getName() + ": advice [" + getAdvice() + "]";
	}

}
