

package org.springframework.web.socket.adapter;

import org.springframework.lang.Nullable;
import org.springframework.web.socket.WebSocketSession;

/**
 * A {@link WebSocketSession} that exposes the underlying, native WebSocketSession
 * through a getter.
 *
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public interface NativeWebSocketSession extends WebSocketSession {

	/**
	 * Return the underlying native WebSocketSession.
	 */
	Object getNativeSession();

	/**
	 * Return the underlying native WebSocketSession, if available.
	 * @param requiredType the required type of the session
	 * @return the native session of the required type,
	 * or {@code null} if not available
	 */
	@Nullable
	<T> T getNativeSession(@Nullable Class<T> requiredType);

}
