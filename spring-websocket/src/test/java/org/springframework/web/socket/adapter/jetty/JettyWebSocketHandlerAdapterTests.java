

package org.springframework.web.socket.adapter.jetty;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;

import static org.mockito.BDDMockito.*;

/**
 * Test fixture for {@link org.springframework.web.socket.adapter.jetty.JettyWebSocketHandlerAdapter}.
 *
 *
 */
public class JettyWebSocketHandlerAdapterTests {

	private JettyWebSocketHandlerAdapter adapter;

	private WebSocketHandler webSocketHandler;

	private JettyWebSocketSession webSocketSession;

	private Session session;


	@Before
	public void setup() {
		this.session = mock(Session.class);
		given(this.session.getUpgradeRequest()).willReturn(Mockito.mock(UpgradeRequest.class));
		given(this.session.getUpgradeResponse()).willReturn(Mockito.mock(UpgradeResponse.class));

		this.webSocketHandler = mock(WebSocketHandler.class);
		this.webSocketSession = new JettyWebSocketSession(null, null);
		this.adapter = new JettyWebSocketHandlerAdapter(this.webSocketHandler, this.webSocketSession);
	}

	@Test
	public void onOpen() throws Throwable {
		this.adapter.onWebSocketConnect(this.session);
		verify(this.webSocketHandler).afterConnectionEstablished(this.webSocketSession);
	}

	@Test
	public void onClose() throws Throwable {
		this.adapter.onWebSocketClose(1000, "reason");
		verify(this.webSocketHandler).afterConnectionClosed(this.webSocketSession, CloseStatus.NORMAL.withReason("reason"));
	}

	@Test
	public void onError() throws Throwable {
		Exception exception = new Exception();
		this.adapter.onWebSocketError(exception);
		verify(this.webSocketHandler).handleTransportError(this.webSocketSession, exception);
	}

}
