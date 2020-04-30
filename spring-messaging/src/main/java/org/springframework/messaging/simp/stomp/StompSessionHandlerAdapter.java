

package org.springframework.messaging.simp.stomp;

import java.lang.reflect.Type;

import org.springframework.lang.Nullable;

/**
 * Abstract adapter class for {@link StompSessionHandler} with mostly empty
 * implementation methods except for {@link #getPayloadType} which returns String
 * as the default Object type expected for STOMP ERROR frame payloads.
 *
 *
 * @since 4.2
 */
public abstract class StompSessionHandlerAdapter implements StompSessionHandler {

	/**
	 * This implementation returns String as the expected payload type
	 * for STOMP ERROR frames.
	 */
	@Override
	public Type getPayloadType(StompHeaders headers) {
		return String.class;
	}

	/**
	 * This implementation is empty.
	 */
	@Override
	public void handleFrame(StompHeaders headers, @Nullable Object payload) {
	}

	/**
	 * This implementation is empty.
	 */
	@Override
	public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
	}

	/**
	 * This implementation is empty.
	 */
	@Override
	public void handleException(StompSession session, @Nullable StompCommand command,
			StompHeaders headers, byte[] payload, Throwable exception) {
	}

	/**
	 * This implementation is empty.
	 */
	@Override
	public void handleTransportError(StompSession session, Throwable exception) {
	}

}
