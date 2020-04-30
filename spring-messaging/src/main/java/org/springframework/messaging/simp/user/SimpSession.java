

package org.springframework.messaging.simp.user;

import java.util.Set;

/**
 * Represents a session of connected user.
 *
 *
 * @since 4.2
 */
public interface SimpSession {

	/**
	 * Return the session id.
	 */
	String getId();

	/**
	 * Return the user associated with the session.
	 */
	SimpUser getUser();

	/**
	 * Return the subscriptions for this session.
	 */
	Set<SimpSubscription> getSubscriptions();

}
