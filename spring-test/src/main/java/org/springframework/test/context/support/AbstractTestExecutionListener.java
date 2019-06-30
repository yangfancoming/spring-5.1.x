

package org.springframework.test.context.support;

import org.springframework.core.Ordered;
import org.springframework.test.context.TestExecutionListener;

/**
 * Abstract {@linkplain Ordered ordered} implementation of the
 * {@link TestExecutionListener} API.
 *
 * @author Sam Brannen
 * @since 2.5
 * @see #getOrder()
 */
public abstract class AbstractTestExecutionListener implements TestExecutionListener, Ordered {

	/**
	 * The default implementation returns {@link Ordered#LOWEST_PRECEDENCE},
	 * thereby ensuring that custom listeners are ordered after default
	 * listeners supplied by the framework. Can be overridden by subclasses
	 * as necessary.
	 * @since 4.1
	 */
	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

}
