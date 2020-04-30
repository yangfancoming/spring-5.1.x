

package org.springframework.messaging.tcp;

import org.springframework.lang.Nullable;

/**
 * A contract to determine the frequency of reconnect attempts after connection failure.
 *
 *
 * @since 4.0
 */
@FunctionalInterface
public interface ReconnectStrategy {

	/**
	 * Return the time to the next attempt to reconnect.
	 * @param attemptCount how many reconnect attempts have been made already
	 * @return the amount of time in milliseconds, or {@code null} to stop
	 */
	@Nullable
	Long getTimeToNextAttempt(int attemptCount);

}
