

package org.springframework.messaging.support;

/**
 * Callback interface for initializing a {@link MessageHeaderAccessor}.
 *
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public interface MessageHeaderInitializer {

	/**
	 * Initialize the given {@code MessageHeaderAccessor}.
	 * @param headerAccessor the MessageHeaderAccessor to initialize
	 */
	void initHeaders(MessageHeaderAccessor headerAccessor);

}
