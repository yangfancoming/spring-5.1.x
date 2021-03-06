

package org.springframework.web.socket.client.jetty;

import java.net.URI;
import java.util.Arrays;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.jetty.JettyWebSocketHandlerAdapter;
import org.springframework.web.socket.adapter.jetty.JettyWebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import static org.junit.Assert.*;

/**
 * Tests for {@link JettyWebSocketClient}.
 *
 */
public class JettyWebSocketClientTests {

	private JettyWebSocketClient client;

	private TestJettyWebSocketServer server;

	private String wsUrl;

	private WebSocketSession wsSession;


	@Before
	public void setup() throws Exception {

		this.server = new TestJettyWebSocketServer(new TextWebSocketHandler());
		this.server.start();

		this.client = new JettyWebSocketClient();
		this.client.start();

		this.wsUrl = "ws://localhost:" + this.server.getPort() + "/test";
	}

	@After
	public void teardown() throws Exception {
		this.wsSession.close();
		this.client.stop();
		this.server.stop();
	}


	@Test
	public void doHandshake() throws Exception {

		WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
		headers.setSecWebSocketProtocol(Arrays.asList("echo"));

		this.wsSession = this.client.doHandshake(new TextWebSocketHandler(), headers, new URI(this.wsUrl)).get();

		assertEquals(this.wsUrl, this.wsSession.getUri().toString());
		assertEquals("echo", this.wsSession.getAcceptedProtocol());
	}

	@Test
	public void doHandshakeWithTaskExecutor() throws Exception {

		WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
		headers.setSecWebSocketProtocol(Arrays.asList("echo"));

		this.client.setTaskExecutor(new SimpleAsyncTaskExecutor());
		this.wsSession = this.client.doHandshake(new TextWebSocketHandler(), headers, new URI(this.wsUrl)).get();

		assertEquals(this.wsUrl, this.wsSession.getUri().toString());
		assertEquals("echo", this.wsSession.getAcceptedProtocol());
	}


	private static class TestJettyWebSocketServer {

		private final Server server;


		public TestJettyWebSocketServer(final WebSocketHandler webSocketHandler) {

			this.server = new Server();
			ServerConnector connector = new ServerConnector(this.server);
			connector.setPort(0);

			this.server.addConnector(connector);
			this.server.setHandler(new org.eclipse.jetty.websocket.server.WebSocketHandler() {
				@Override
				public void configure(WebSocketServletFactory factory) {
					factory.setCreator(new WebSocketCreator() {
						@Override
						public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
							if (!CollectionUtils.isEmpty(req.getSubProtocols())) {
								resp.setAcceptedSubProtocol(req.getSubProtocols().get(0));
							}
							JettyWebSocketSession session = new JettyWebSocketSession(null, null);
							return new JettyWebSocketHandlerAdapter(webSocketHandler, session);
						}
					});
				}
			});
		}

		public void start() throws Exception {
			this.server.start();
		}

		public void stop() throws Exception {
			this.server.stop();
		}

		public int getPort() {
			return ((ServerConnector) this.server.getConnectors()[0]).getLocalPort();
		}
	}

}
