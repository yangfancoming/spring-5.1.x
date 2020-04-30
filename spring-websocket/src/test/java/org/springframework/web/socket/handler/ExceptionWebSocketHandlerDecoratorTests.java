

package org.springframework.web.socket.handler;

import org.junit.Before;
import org.junit.Test;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

/**
 * Test fixture for {@link ExceptionWebSocketHandlerDecorator}.
 *
 *
 */
public class ExceptionWebSocketHandlerDecoratorTests {

	private TestWebSocketSession session;

	private ExceptionWebSocketHandlerDecorator decorator;

	private WebSocketHandler delegate;


	@Before
	public void setup() {

		this.delegate = mock(WebSocketHandler.class);
		this.decorator = new ExceptionWebSocketHandlerDecorator(this.delegate);

		this.session = new TestWebSocketSession();
		this.session.setOpen(true);
	}

	@Test
	public void afterConnectionEstablished() throws Exception {

		willThrow(new IllegalStateException("error"))
			.given(this.delegate).afterConnectionEstablished(this.session);

		this.decorator.afterConnectionEstablished(this.session);

		assertEquals(CloseStatus.SERVER_ERROR, this.session.getCloseStatus());
	}

	@Test
	public void handleMessage() throws Exception {

		TextMessage message = new TextMessage("payload");

		willThrow(new IllegalStateException("error"))
			.given(this.delegate).handleMessage(this.session, message);

		this.decorator.handleMessage(this.session, message);

		assertEquals(CloseStatus.SERVER_ERROR, this.session.getCloseStatus());
	}

	@Test
	public void handleTransportError() throws Exception {

		Exception exception = new Exception("transport error");

		willThrow(new IllegalStateException("error"))
			.given(this.delegate).handleTransportError(this.session, exception);

		this.decorator.handleTransportError(this.session, exception);

		assertEquals(CloseStatus.SERVER_ERROR, this.session.getCloseStatus());
	}

	@Test
	public void afterConnectionClosed() throws Exception {

		CloseStatus closeStatus = CloseStatus.NORMAL;

		willThrow(new IllegalStateException("error"))
			.given(this.delegate).afterConnectionClosed(this.session, closeStatus);

		this.decorator.afterConnectionClosed(this.session, closeStatus);

		assertNull(this.session.getCloseStatus());
	}

}
