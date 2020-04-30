

package org.springframework.web.server.adapter;

import org.junit.Test;

import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.test.MockServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.i18n.AcceptHeaderLocaleContextResolver;
import org.springframework.web.server.session.DefaultWebSessionManager;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link DefaultServerWebExchange}.
 *
 * @author Arjen Poutsma
 *
 */
public class DefaultServerWebExchangeTests {

	@Test
	public void transformUrlDefault() {
		ServerWebExchange exchange = createExchange();
		assertEquals("/foo", exchange.transformUrl("/foo"));
	}

	@Test
	public void transformUrlWithEncoder() {
		ServerWebExchange exchange = createExchange();
		exchange.addUrlTransformer(s -> s + "?nonce=123");
		assertEquals("/foo?nonce=123", exchange.transformUrl("/foo"));
	}

	@Test
	public void transformUrlWithMultipleEncoders() {
		ServerWebExchange exchange = createExchange();
		exchange.addUrlTransformer(s -> s + ";p=abc");
		exchange.addUrlTransformer(s -> s + "?q=123");
		assertEquals("/foo;p=abc?q=123", exchange.transformUrl("/foo"));
	}


	private DefaultServerWebExchange createExchange() {
		MockServerHttpRequest request = MockServerHttpRequest.get("https://example.com").build();
		return createExchange(request);
	}

	private DefaultServerWebExchange createExchange(MockServerHttpRequest request) {
		return new DefaultServerWebExchange(request, new MockServerHttpResponse(),
				new DefaultWebSessionManager(), ServerCodecConfigurer.create(),
				new AcceptHeaderLocaleContextResolver());
	}

}
