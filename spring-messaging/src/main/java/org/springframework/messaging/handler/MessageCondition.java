

package org.springframework.messaging.handler;

import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;

/**
 * Contract for mapping conditions to messages.
 *
 * Message conditions can be combined (e.g. type + method-level conditions),
 * matched to a specific Message, as well as compared to each other in the
 * context of a Message to determine which one matches a request more closely.
 *
 *
 * @since 4.0
 * @param <T> the kind of condition that this condition can be combined with or compared to
 */
public interface MessageCondition<T> {

	/**
	 * Define the rules for combining this condition with another.
	 * For example combining type- and method-level conditions.
	 * @param other the condition to combine with
	 * @return the resulting message condition
	 */
	T combine(T other);

	/**
	 * Check if this condition matches the given Message and returns a
	 * potentially new condition with content tailored to the current message.
	 * For example a condition with destination patterns might return a new
	 * condition with sorted, matching patterns only.
	 * @return a condition instance in case of a match; or {@code null} if there is no match.
	 */
	@Nullable
	T getMatchingCondition(Message<?> message);

	/**
	 * Compare this condition to another in the context of a specific message.
	 * It is assumed both instances have been obtained via
	 * {@link #getMatchingCondition(Message)} to ensure they have content
	 * relevant to current message only.
	 */
	int compareTo(T other, Message<?> message);

}
