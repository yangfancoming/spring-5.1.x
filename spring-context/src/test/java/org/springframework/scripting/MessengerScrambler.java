

package org.springframework.scripting;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * Twee advice that 'scrambles' the return value
 * of a {@link Messenger} invocation.
 *
 * @author Rick Evans
 */
public class MessengerScrambler {

	public String scramble(ProceedingJoinPoint pjp) throws Throwable {
		String message = (String) pjp.proceed();
		return new StringBuffer(message).reverse().toString();
	}

}
