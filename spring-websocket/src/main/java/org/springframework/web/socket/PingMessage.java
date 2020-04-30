

package org.springframework.web.socket;

import java.nio.ByteBuffer;

/**
 * A WebSocket ping message.
 *
 *
 * @since 4.0
 */
public final class PingMessage extends AbstractWebSocketMessage<ByteBuffer> {

	/**
	 * Create a new ping message with an empty payload.
	 */
	public PingMessage() {
		super(ByteBuffer.allocate(0));
	}

	/**
	 * Create a new ping message with the given ByteBuffer payload.
	 * @param payload the non-null payload
	 */
	public PingMessage(ByteBuffer payload) {
		super(payload);
	}


	@Override
	public int getPayloadLength() {
		return getPayload().remaining();
	}

	@Override
	protected String toStringPayload() {
		return getPayload().toString();
	}

}
