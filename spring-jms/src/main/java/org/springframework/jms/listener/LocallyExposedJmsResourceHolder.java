

package org.springframework.jms.listener;

import javax.jms.Session;

import org.springframework.jms.connection.JmsResourceHolder;

/**
 * {@link JmsResourceHolder} marker subclass that indicates local exposure,
 * i.e. that does not indicate an externally managed transaction.
 *

 * @since 2.5.2
 */
class LocallyExposedJmsResourceHolder extends JmsResourceHolder {

	public LocallyExposedJmsResourceHolder(Session session) {
		super(session);
	}

}
