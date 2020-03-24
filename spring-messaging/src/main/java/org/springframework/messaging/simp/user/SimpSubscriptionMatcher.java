

package org.springframework.messaging.simp.user;

/**
 * A strategy for matching subscriptions.
 *
 * @author Rossen Stoyanchev
 * @since 4.2
 */
@FunctionalInterface
public interface SimpSubscriptionMatcher {

	/**
	 * Match the given subscription.
	 * @param subscription the subscription to match
	 * @return {@code true} in case of a match, {@code false} otherwise
	 */
	boolean match(SimpSubscription subscription);

}
