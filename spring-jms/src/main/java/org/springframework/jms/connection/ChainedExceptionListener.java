

package org.springframework.jms.connection;

import java.util.ArrayList;
import java.util.List;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import org.springframework.util.Assert;

/**
 * Implementation of the JMS ExceptionListener interface that supports chaining,
 * allowing the addition of multiple ExceptionListener instances in order.
 *

 * @since 2.0
 */
public class ChainedExceptionListener implements ExceptionListener {

	/** List of ExceptionListeners. */
	private final List<ExceptionListener> delegates = new ArrayList<>(2);


	/**
	 * Add an ExceptionListener to the chained delegate list.
	 */
	public final void addDelegate(ExceptionListener listener) {
		Assert.notNull(listener, "ExceptionListener must not be null");
		this.delegates.add(listener);
	}

	/**
	 * Return all registered ExceptionListener delegates (as array).
	 */
	public final ExceptionListener[] getDelegates() {
		return this.delegates.toArray(new ExceptionListener[0]);
	}


	@Override
	public void onException(JMSException ex) {
		for (ExceptionListener listener : this.delegates) {
			listener.onException(ex);
		}
	}

}
