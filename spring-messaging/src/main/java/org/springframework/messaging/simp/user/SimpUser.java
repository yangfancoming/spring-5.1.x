

package org.springframework.messaging.simp.user;

import java.util.Set;

import org.springframework.lang.Nullable;

/**
 * Represents a connected user.
 *
 *
 * @since 4.2
 */
public interface SimpUser {

	/**
	 * The unique user name.
	 */
	String getName();

	/**
	 * Whether the user has any sessions.
	 */
	boolean hasSessions();

	/**
	 * Look up the session for the given id.
	 * @param sessionId the session id
	 * @return the matching session, or {@code null} if none found
	 */
	@Nullable
	SimpSession getSession(String sessionId);

	/**
	 * Return the sessions for the user.
	 * The returned set is a copy and will never be modified.
	 * @return a set of session ids, or an empty set if none
	 */
	Set<SimpSession> getSessions();

}
