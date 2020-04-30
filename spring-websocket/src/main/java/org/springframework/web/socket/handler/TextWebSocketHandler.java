

package org.springframework.web.socket.handler;

import java.io.IOException;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;

/**
 * A convenient base class for {@link WebSocketHandler} implementations
 * that process text messages only.
 *
 * xmlBeanDefinitionReaderBinary messages are rejected with {@link CloseStatus#NOT_ACCEPTABLE}.
 * All other methods have empty implementations.
 *
 *
 * @author Phillip Webb
 * @since 4.0
 */
public class TextWebSocketHandler extends AbstractWebSocketHandler {

	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
		try {
			session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Binary messages not supported"));
		}
		catch (IOException ex) {
			// ignore
		}
	}

}
