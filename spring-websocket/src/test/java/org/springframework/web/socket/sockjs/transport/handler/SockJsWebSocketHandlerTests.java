

package org.springframework.web.socket.sockjs.transport.handler;

import java.util.Collections;

import org.junit.Test;

import org.springframework.messaging.SubscribableChannel;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.messaging.StompSubProtocolHandler;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;
import org.springframework.web.socket.sockjs.transport.session.WebSocketServerSockJsSession;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link SockJsWebSocketHandler}.
 *
 * @author Rossen Stoyanchev
 */
public class SockJsWebSocketHandlerTests {

	@Test
	public void getSubProtocols() throws Exception {
		SubscribableChannel channel = mock(SubscribableChannel.class);
		SubProtocolWebSocketHandler handler = new SubProtocolWebSocketHandler(channel, channel);
		StompSubProtocolHandler stompHandler = new StompSubProtocolHandler();
		handler.addProtocolHandler(stompHandler);

		TaskScheduler scheduler = mock(TaskScheduler.class);
		DefaultSockJsService service = new DefaultSockJsService(scheduler);
		WebSocketServerSockJsSession session = new WebSocketServerSockJsSession("1", service, handler, null);
		SockJsWebSocketHandler sockJsHandler = new SockJsWebSocketHandler(service, handler, session);

		assertEquals(stompHandler.getSupportedProtocols(), sockJsHandler.getSubProtocols());
	}

	@Test
	public void getSubProtocolsNone() throws Exception {
		WebSocketHandler handler = new TextWebSocketHandler();
		TaskScheduler scheduler = mock(TaskScheduler.class);
		DefaultSockJsService service = new DefaultSockJsService(scheduler);
		WebSocketServerSockJsSession session = new WebSocketServerSockJsSession("1", service, handler, null);
		SockJsWebSocketHandler sockJsHandler = new SockJsWebSocketHandler(service, handler, session);

		assertEquals(Collections.emptyList(), sockJsHandler.getSubProtocols());
	}

}
