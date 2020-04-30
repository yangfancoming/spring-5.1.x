

package org.springframework.messaging.simp.user;

/**
 * Represents a subscription within a user session.
 *
 *
 * @since 4.2
 */
public interface SimpSubscription {

	/**
	 * Return the id associated of the subscription.
	 */
	String getId();

	/**
	 * Return the session of the subscription.
	 */
	SimpSession getSession();

	/**
	 * Return the subscription's destination.
	 */
	String getDestination();

}
