
package org.springframework.web.server.session;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests using {@link HeaderWebSessionIdResolver}.
 *
 * @author Greg Turnquist
 * @author Rob Winch
 */
public class HeaderWebSessionIdResolverTests {
	private HeaderWebSessionIdResolver idResolver;

	private ServerWebExchange exchange;

	@Before
	public void setUp() {
		this.idResolver = new HeaderWebSessionIdResolver();
		this.exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/path"));
	}

	@Test
	public void expireWhenValidThenSetsEmptyHeader() {
		this.idResolver.expireSession(this.exchange);

		assertEquals(Arrays.asList(""),
				this.exchange.getResponse().getHeaders().get(HeaderWebSessionIdResolver.DEFAULT_HEADER_NAME));
	}

	@Test
	public void expireWhenMultipleInvocationThenSetsSingleEmptyHeader() {
		this.idResolver.expireSession(this.exchange);

		this.idResolver.expireSession(this.exchange);

		assertEquals(Arrays.asList(""),
				this.exchange.getResponse().getHeaders().get(HeaderWebSessionIdResolver.DEFAULT_HEADER_NAME));
	}

	@Test
	public void expireWhenAfterSetSessionIdThenSetsEmptyHeader() {
		this.idResolver.setSessionId(this.exchange, "123");

		this.idResolver.expireSession(this.exchange);

		assertEquals(Arrays.asList(""),
				this.exchange.getResponse().getHeaders().get(HeaderWebSessionIdResolver.DEFAULT_HEADER_NAME));
	}

	@Test
	public void setSessionIdWhenValidThenSetsHeader() {
		String id = "123";

		this.idResolver.setSessionId(this.exchange, id);

		assertEquals(Arrays.asList(id),
				this.exchange.getResponse().getHeaders().get(HeaderWebSessionIdResolver.DEFAULT_HEADER_NAME));
	}

	@Test
	public void setSessionIdWhenMultipleThenSetsSingleHeader() {
		String id = "123";
		this.idResolver.setSessionId(this.exchange, "overriddenByNextInvocation");

		this.idResolver.setSessionId(this.exchange, id);

		assertEquals(Arrays.asList(id),
				this.exchange.getResponse().getHeaders().get(HeaderWebSessionIdResolver.DEFAULT_HEADER_NAME));
	}

	@Test
	public void setSessionIdWhenCustomHeaderNameThenSetsHeader() {
		String headerName = "x-auth";
		String id = "123";
		this.idResolver.setHeaderName(headerName);

		this.idResolver.setSessionId(this.exchange, id);

		assertEquals(Arrays.asList(id),
				this.exchange.getResponse().getHeaders().get(headerName));
	}

	@Test(expected = IllegalArgumentException.class)
	public void setSessionIdWhenNullIdThenIllegalArgumentException() {
		String id = null;

		this.idResolver.setSessionId(this.exchange, id);
	}

	@Test
	public void resolveSessionIdsWhenNoIdsThenEmpty() {
		List<String> ids = this.idResolver.resolveSessionIds(this.exchange);

		assertTrue(ids.isEmpty());
	}

	@Test
	public void resolveSessionIdsWhenIdThenIdFound() {
		String id = "123";
		this.exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/path")
				.header(HeaderWebSessionIdResolver.DEFAULT_HEADER_NAME, id));

		List<String> ids = this.idResolver.resolveSessionIds(this.exchange);

		assertEquals(Arrays.asList(id), ids);
	}

	@Test
	public void resolveSessionIdsWhenMultipleIdsThenIdsFound() {
		String id1 = "123";
		String id2 = "abc";
		this.exchange = MockServerWebExchange.from(
				MockServerHttpRequest.get("/path")
						.header(HeaderWebSessionIdResolver.DEFAULT_HEADER_NAME, id1, id2));

		List<String> ids = this.idResolver.resolveSessionIds(this.exchange);

		assertEquals(Arrays.asList(id1, id2), ids);
	}
}
