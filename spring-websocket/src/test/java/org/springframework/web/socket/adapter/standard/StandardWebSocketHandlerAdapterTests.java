

package org.springframework.web.socket.adapter.standard;

import java.net.URI;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.junit.Before;
import org.junit.Test;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

/**
 * Test fixture for {@link org.springframework.web.socket.adapter.standard.StandardWebSocketHandlerAdapter}.
 *
 * @author Rossen Stoyanchev
 */
public class StandardWebSocketHandlerAdapterTests {

	private StandardWebSocketHandlerAdapter adapter;

	private WebSocketHandler webSocketHandler;

	private StandardWebSocketSession webSocketSession;

	private Session session;


	@Before
	public void setup() {
		this.session = mock(Session.class);
		this.webSocketHandler = mock(WebSocketHandler.class);
		this.webSocketSession = new StandardWebSocketSession(null, null, null, null);
		this.adapter = new StandardWebSocketHandlerAdapter(this.webSocketHandler, this.webSocketSession);
	}

	@Test
	public void onOpen() throws Throwable {
		URI uri = URI.create("https://example.org");
		given(this.session.getRequestURI()).willReturn(uri);
		this.adapter.onOpen(this.session, null);

		verify(this.webSocketHandler).afterConnectionEstablished(this.webSocketSession);
		verify(this.session, atLeast(2)).addMessageHandler(any(MessageHandler.Whole.class));

		given(this.session.getRequestURI()).willReturn(uri);
		assertEquals(uri, this.webSocketSession.getUri());
	}

	@Test
	public void onClose() throws Throwable {
		this.adapter.onClose(this.session, new CloseReason(CloseCodes.NORMAL_CLOSURE, "reason"));
		verify(this.webSocketHandler).afterConnectionClosed(this.webSocketSession, CloseStatus.NORMAL.withReason("reason"));
	}

	@Test
	public void onError() throws Throwable {
		Exception exception = new Exception();
		this.adapter.onError(this.session, exception);
		verify(this.webSocketHandler).handleTransportError(this.webSocketSession, exception);
	}

}
