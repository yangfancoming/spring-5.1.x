
package org.springframework.web.server.session;

import org.junit.Test;

import org.springframework.http.ResponseCookie;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.util.MultiValueMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for {@link CookieWebSessionIdResolver}.
 * @author Rossen Stoyanchev
 */
public class CookieWebSessionIdResolverTests {

	private final CookieWebSessionIdResolver resolver = new CookieWebSessionIdResolver();


	@Test
	public void setSessionId() {
		MockServerHttpRequest request = MockServerHttpRequest.get("https://example.org/path").build();
		MockServerWebExchange exchange = MockServerWebExchange.from(request);
		this.resolver.setSessionId(exchange, "123");

		MultiValueMap<String, ResponseCookie> cookies = exchange.getResponse().getCookies();
		assertEquals(1, cookies.size());
		ResponseCookie cookie = cookies.getFirst(this.resolver.getCookieName());
		assertNotNull(cookie);
		assertEquals("SESSION=123; Path=/; Secure; HttpOnly; SameSite=Lax", cookie.toString());
	}

	@Test
	public void cookieInitializer() {
		this.resolver.addCookieInitializer(builder -> builder.domain("example.org"));
		this.resolver.addCookieInitializer(builder -> builder.sameSite("Strict"));
		this.resolver.addCookieInitializer(builder -> builder.secure(false));

		MockServerHttpRequest request = MockServerHttpRequest.get("https://example.org/path").build();
		MockServerWebExchange exchange = MockServerWebExchange.from(request);
		this.resolver.setSessionId(exchange, "123");

		MultiValueMap<String, ResponseCookie> cookies = exchange.getResponse().getCookies();
		assertEquals(1, cookies.size());
		ResponseCookie cookie = cookies.getFirst(this.resolver.getCookieName());
		assertNotNull(cookie);
		assertEquals("SESSION=123; Path=/; Domain=example.org; HttpOnly; SameSite=Strict", cookie.toString());
	}

}
