

package org.springframework.web.socket.handler;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test fixture for {@link WebSocketHandlerDecorator}.
 *
 *
 */
public class WebSocketHandlerDecoratorTests {

	@Test
	public void getLastHandler() {
		AbstractWebSocketHandler h1 = new AbstractWebSocketHandler() {
		};
		WebSocketHandlerDecorator h2 = new WebSocketHandlerDecorator(h1);
		WebSocketHandlerDecorator h3 = new WebSocketHandlerDecorator(h2);

		assertSame(h1, h3.getLastHandler());
	}

}
