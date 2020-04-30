

package org.springframework.web.reactive.result.view;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import org.springframework.context.support.GenericApplicationContext;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link RequestContext}.
 *
 */
public class RequestContextTests {

	private final MockServerWebExchange exchange =
			MockServerWebExchange.from(MockServerHttpRequest.get("/foo/path").contextPath("/foo"));

	private GenericApplicationContext applicationContext;

	private Map<String, Object> model = new HashMap<>();


	@Before
	public void init() {
		this.applicationContext = new GenericApplicationContext();
		this.applicationContext.refresh();
	}

	@Test
	public void testGetContextUrl() throws Exception {
		RequestContext context = new RequestContext(this.exchange, this.model, this.applicationContext);
		assertEquals("/foo/bar", context.getContextUrl("bar"));
	}

	@Test
	public void testGetContextUrlWithMap() throws Exception {
		RequestContext context = new RequestContext(this.exchange, this.model, this.applicationContext);
		Map<String, Object> map = new HashMap<>();
		map.put("foo", "bar");
		map.put("spam", "bucket");
		assertEquals("/foo/bar?spam=bucket", context.getContextUrl("{foo}?spam={spam}", map));
	}

	@Test
	public void testGetContextUrlWithMapEscaping() throws Exception {
		RequestContext context = new RequestContext(this.exchange, this.model, this.applicationContext);
		Map<String, Object> map = new HashMap<>();
		map.put("foo", "bar baz");
		map.put("spam", "&bucket=");
		assertEquals("/foo/bar%20baz?spam=%26bucket%3D", context.getContextUrl("{foo}?spam={spam}", map));
	}

}
