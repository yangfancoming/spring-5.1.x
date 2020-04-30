

package org.springframework.web.socket.handler;

import org.junit.Test;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import static org.junit.Assert.*;

/**
 * Test fixture for {@link PerConnectionWebSocketHandler}.
 *
 *
 */
public class PerConnectionWebSocketHandlerTests {


	@Test
	public void afterConnectionEstablished() throws Exception {

		@SuppressWarnings("resource")
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.refresh();

		EchoHandler.reset();
		PerConnectionWebSocketHandler handler = new PerConnectionWebSocketHandler(EchoHandler.class);
		handler.setBeanFactory(context.getBeanFactory());

		WebSocketSession session = new TestWebSocketSession();
		handler.afterConnectionEstablished(session);

		assertEquals(1, EchoHandler.initCount);
		assertEquals(0, EchoHandler.destroyCount);

		handler.afterConnectionClosed(session, CloseStatus.NORMAL);

		assertEquals(1, EchoHandler.initCount);
		assertEquals(1, EchoHandler.destroyCount);
	}


	public static class EchoHandler extends AbstractWebSocketHandler implements DisposableBean {

		private static int initCount;

		private static int destroyCount;


		public EchoHandler() {
			initCount++;
		}

		@Override
		public void destroy() throws Exception {
			destroyCount++;
		}

		public static void reset() {
			initCount = 0;
			destroyCount = 0;
		}
	}

}
