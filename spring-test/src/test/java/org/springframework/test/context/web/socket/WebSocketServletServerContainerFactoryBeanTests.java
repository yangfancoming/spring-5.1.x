

package org.springframework.test.context.web.socket;

import javax.websocket.server.ServerContainer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import static org.junit.Assert.*;

/**
 * Integration tests that validate support for {@link ServletServerContainerFactoryBean}
 * in conjunction with {@link WebAppConfiguration @WebAppConfiguration} and the
 * Spring TestContext Framework.
 *
 * @author Sam Brannen
 * @since 4.3.1
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
public class WebSocketServletServerContainerFactoryBeanTests {

	@Autowired
	ServerContainer serverContainer;


	@Test
	public void servletServerContainerFactoryBeanSupport() {
		assertEquals(42, serverContainer.getDefaultMaxTextMessageBufferSize());
	}


	@Configuration
	@EnableWebSocket
	static class WebSocketConfig {

		@Bean
		ServletServerContainerFactoryBean createWebSocketContainer() {
			ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
			container.setMaxTextMessageBufferSize(42);
			return container;
		}
	}

}
