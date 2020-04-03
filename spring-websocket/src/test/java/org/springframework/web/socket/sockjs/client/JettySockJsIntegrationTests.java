

package org.springframework.web.socket.sockjs.client;

import org.eclipse.jetty.client.HttpClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.JettyWebSocketTestServer;
import org.springframework.web.socket.client.jetty.JettyWebSocketClient;
import org.springframework.web.socket.server.RequestUpgradeStrategy;
import org.springframework.web.socket.server.jetty.JettyRequestUpgradeStrategy;

/**
 * SockJS integration tests using Jetty for client and server.
 *
 * @author Rossen Stoyanchev
 */
public class JettySockJsIntegrationTests extends AbstractSockJsIntegrationTests {

	@Override
	protected Class<?> upgradeStrategyConfigClass() {
		return JettyTestConfig.class;
	}

	@Override
	protected JettyWebSocketTestServer createWebSocketTestServer() {
		return new JettyWebSocketTestServer();
	}

	@Override
	protected Transport createWebSocketTransport() {
		return new WebSocketTransport(new JettyWebSocketClient());
	}

	@Override
	protected AbstractXhrTransport createXhrTransport() {
		return new JettyXhrTransport(new HttpClient());
	}


	@Configuration
	static class JettyTestConfig {
		@Bean
		public RequestUpgradeStrategy upgradeStrategy() {
			return new JettyRequestUpgradeStrategy();
		}
	}

}
