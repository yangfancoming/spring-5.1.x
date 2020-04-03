

package org.springframework.orm.hibernate5;

import org.hibernate.Session;

import org.springframework.transaction.support.TransactionSynchronizationAdapter;

/**
 * Simple synchronization adapter that propagates a {@code flush()} call
 * to the underlying Hibernate Session. Used in combination with JTA.
 *

 * @since 4.2
 */
public class SpringFlushSynchronization extends TransactionSynchronizationAdapter {

	private final Session session;


	public SpringFlushSynchronization(Session session) {
		this.session = session;
	}


	@Override
	public void flush() {
		SessionFactoryUtils.flush(this.session, false);
	}


	@Override
	public boolean equals(Object other) {
		return (this == other || (other instanceof SpringFlushSynchronization &&
				this.session == ((SpringFlushSynchronization) other).session));
	}

	@Override
	public int hashCode() {
		return this.session.hashCode();
	}

}
