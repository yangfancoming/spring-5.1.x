

package org.springframework.web.cors.reactive;

import org.junit.Test;

import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.web.cors.CorsConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit tests for {@link UrlBasedCorsConfigurationSource}.
 *
 * @author Sebastien Deleuze
 * @author Rossen Stoyanchev
 */
public class UrlBasedCorsConfigurationSourceTests {

	private final UrlBasedCorsConfigurationSource configSource
			= new UrlBasedCorsConfigurationSource();


	@Test
	public void empty() {
		MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/bar/test.html"));
		assertNull(this.configSource.getCorsConfiguration(exchange));
	}

	@Test
	public void registerAndMatch() {
		CorsConfiguration config = new CorsConfiguration();
		this.configSource.registerCorsConfiguration("/bar/**", config);

		MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/foo/test.html"));
		assertNull(this.configSource.getCorsConfiguration(exchange));

		exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/bar/test.html"));
		assertEquals(config, this.configSource.getCorsConfiguration(exchange));
	}

}
