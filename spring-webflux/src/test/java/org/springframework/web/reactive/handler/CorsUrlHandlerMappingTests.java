
package org.springframework.web.reactive.handler;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * Unit tests for CORS support at {@link AbstractUrlHandlerMapping} level.
 *
 * @author Sebastien Deleuze
 * @author Rossen Stoyanchev
 */
public class CorsUrlHandlerMappingTests {

	private AbstractUrlHandlerMapping handlerMapping;

	private Object welcomeController = new Object();

	private CorsAwareHandler corsController = new CorsAwareHandler();


	@Before
	public void setup() {
		this.handlerMapping = new AbstractUrlHandlerMapping() {};
		this.handlerMapping.registerHandler("/welcome.html", this.welcomeController);
		this.handlerMapping.registerHandler("/cors.html", this.corsController);
	}


	@Test
	public void actualRequestWithoutCorsConfigurationProvider() throws Exception {
		String origin = "https://domain2.com";
		ServerWebExchange exchange = createExchange(HttpMethod.GET, "/welcome.html", origin);
		Object actual = this.handlerMapping.getHandler(exchange).block();

		assertNotNull(actual);
		assertSame(this.welcomeController, actual);
	}

	@Test
	public void preflightRequestWithoutCorsConfigurationProvider() throws Exception {
		String origin = "https://domain2.com";
		ServerWebExchange exchange = createExchange(HttpMethod.OPTIONS, "/welcome.html", origin);
		Object actual = this.handlerMapping.getHandler(exchange).block();

		assertNotNull(actual);
		assertNotSame(this.welcomeController, actual);
		assertNull(exchange.getResponse().getHeaders().getFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
	}

	@Test
	public void actualRequestWithCorsAwareHandler() throws Exception {
		String origin = "https://domain2.com";
		ServerWebExchange exchange = createExchange(HttpMethod.GET, "/cors.html", origin);
		Object actual = this.handlerMapping.getHandler(exchange).block();

		assertNotNull(actual);
		assertSame(this.corsController, actual);
		assertEquals("*", exchange.getResponse().getHeaders().getFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
	}

	@Test
	public void preFlightWithCorsAwareHandler() throws Exception {
		String origin = "https://domain2.com";
		ServerWebExchange exchange = createExchange(HttpMethod.OPTIONS, "/cors.html", origin);
		Object actual = this.handlerMapping.getHandler(exchange).block();

		assertNotNull(actual);
		assertNotSame(this.corsController, actual);
		assertEquals("*", exchange.getResponse().getHeaders().getFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
	}

	@Test
	public void actualRequestWithGlobalCorsConfig() throws Exception {
		CorsConfiguration mappedConfig = new CorsConfiguration();
		mappedConfig.addAllowedOrigin("*");
		this.handlerMapping.setCorsConfigurations(Collections.singletonMap("/welcome.html", mappedConfig));

		String origin = "https://domain2.com";
		ServerWebExchange exchange = createExchange(HttpMethod.GET, "/welcome.html", origin);
		Object actual = this.handlerMapping.getHandler(exchange).block();

		assertNotNull(actual);
		assertSame(this.welcomeController, actual);
		assertEquals("*", exchange.getResponse().getHeaders().getFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
	}

	@Test
	public void preFlightRequestWithGlobalCorsConfig() throws Exception {
		CorsConfiguration mappedConfig = new CorsConfiguration();
		mappedConfig.addAllowedOrigin("*");
		this.handlerMapping.setCorsConfigurations(Collections.singletonMap("/welcome.html", mappedConfig));

		String origin = "https://domain2.com";
		ServerWebExchange exchange = createExchange(HttpMethod.OPTIONS, "/welcome.html", origin);
		Object actual = this.handlerMapping.getHandler(exchange).block();

		assertNotNull(actual);
		assertNotSame(this.welcomeController, actual);
		assertEquals("*", exchange.getResponse().getHeaders().getFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
	}

	@Test
	public void actualRequestWithCorsConfigurationSource() throws Exception {
		this.handlerMapping.setCorsConfigurationSource(new CustomCorsConfigurationSource());

		String origin = "https://domain2.com";
		ServerWebExchange exchange = createExchange(HttpMethod.GET, "/welcome.html", origin);
		Object actual = this.handlerMapping.getHandler(exchange).block();

		assertNotNull(actual);
		assertSame(this.welcomeController, actual);
		assertEquals("https://domain2.com", exchange.getResponse().getHeaders()
				.getFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
		assertEquals("true", exchange.getResponse().getHeaders()
				.getFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS));
	}

	@Test
	public void preFlightRequestWithCorsConfigurationSource() throws Exception {
		this.handlerMapping.setCorsConfigurationSource(new CustomCorsConfigurationSource());

		String origin = "https://domain2.com";
		ServerWebExchange exchange = createExchange(HttpMethod.OPTIONS, "/welcome.html", origin);
		Object actual = this.handlerMapping.getHandler(exchange).block();

		assertNotNull(actual);
		assertNotSame(this.welcomeController, actual);
		assertEquals("https://domain2.com", exchange.getResponse().getHeaders()
				.getFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
		assertEquals("true", exchange.getResponse().getHeaders()
				.getFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS));
	}


	private ServerWebExchange createExchange(HttpMethod method, String path, String origin) {

		return MockServerWebExchange.from(MockServerHttpRequest
				.method(method, "http://localhost" + path)
				.header("Origin", origin)
				.header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"));
	}


	private class CorsAwareHandler implements CorsConfigurationSource {

		@Override
		public CorsConfiguration getCorsConfiguration(ServerWebExchange exchange) {
			CorsConfiguration config = new CorsConfiguration();
			config.addAllowedOrigin("*");
			return config;
		}
	}

	public class CustomCorsConfigurationSource implements CorsConfigurationSource {

		@Override
		public CorsConfiguration getCorsConfiguration(ServerWebExchange exchange) {
			CorsConfiguration config = new CorsConfiguration();
			config.addAllowedOrigin("*");
			config.setAllowCredentials(true);
			return config;
		}
	}

}
