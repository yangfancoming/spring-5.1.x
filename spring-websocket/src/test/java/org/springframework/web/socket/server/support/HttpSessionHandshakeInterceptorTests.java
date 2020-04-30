

package org.springframework.web.socket.server.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.mockito.Mockito;

import org.springframework.mock.web.test.MockHttpSession;
import org.springframework.web.socket.AbstractHttpRequestTests;
import org.springframework.web.socket.WebSocketHandler;

import static org.junit.Assert.*;

/**
 * Test fixture for {@link HttpSessionHandshakeInterceptor}.
 *
 *
 */
public class HttpSessionHandshakeInterceptorTests extends AbstractHttpRequestTests {


	@Test
	public void defaultConstructor() throws Exception {
		Map<String, Object> attributes = new HashMap<>();
		WebSocketHandler wsHandler = Mockito.mock(WebSocketHandler.class);

		this.servletRequest.setSession(new MockHttpSession(null, "123"));
		this.servletRequest.getSession().setAttribute("foo", "bar");
		this.servletRequest.getSession().setAttribute("bar", "baz");

		HttpSessionHandshakeInterceptor interceptor = new HttpSessionHandshakeInterceptor();
		interceptor.beforeHandshake(this.request, this.response, wsHandler, attributes);

		assertEquals(3, attributes.size());
		assertEquals("bar", attributes.get("foo"));
		assertEquals("baz", attributes.get("bar"));
		assertEquals("123", attributes.get(HttpSessionHandshakeInterceptor.HTTP_SESSION_ID_ATTR_NAME));
	}

	@Test
	public void constructorWithAttributeNames() throws Exception {
		Map<String, Object> attributes = new HashMap<>();
		WebSocketHandler wsHandler = Mockito.mock(WebSocketHandler.class);

		this.servletRequest.setSession(new MockHttpSession(null, "123"));
		this.servletRequest.getSession().setAttribute("foo", "bar");
		this.servletRequest.getSession().setAttribute("bar", "baz");

		Set<String> names = Collections.singleton("foo");
		HttpSessionHandshakeInterceptor interceptor = new HttpSessionHandshakeInterceptor(names);
		interceptor.beforeHandshake(this.request, this.response, wsHandler, attributes);

		assertEquals(2, attributes.size());
		assertEquals("bar", attributes.get("foo"));
		assertEquals("123", attributes.get(HttpSessionHandshakeInterceptor.HTTP_SESSION_ID_ATTR_NAME));
	}

	@Test
	public void doNotCopyHttpSessionId() throws Exception {
		Map<String, Object> attributes = new HashMap<>();
		WebSocketHandler wsHandler = Mockito.mock(WebSocketHandler.class);

		this.servletRequest.setSession(new MockHttpSession(null, "123"));
		this.servletRequest.getSession().setAttribute("foo", "bar");

		HttpSessionHandshakeInterceptor interceptor = new HttpSessionHandshakeInterceptor();
		interceptor.setCopyHttpSessionId(false);
		interceptor.beforeHandshake(this.request, this.response, wsHandler, attributes);

		assertEquals(1, attributes.size());
		assertEquals("bar", attributes.get("foo"));
	}


	@Test
	public void doNotCopyAttributes() throws Exception {
		Map<String, Object> attributes = new HashMap<>();
		WebSocketHandler wsHandler = Mockito.mock(WebSocketHandler.class);

		this.servletRequest.setSession(new MockHttpSession(null, "123"));
		this.servletRequest.getSession().setAttribute("foo", "bar");

		HttpSessionHandshakeInterceptor interceptor = new HttpSessionHandshakeInterceptor();
		interceptor.setCopyAllAttributes(false);
		interceptor.beforeHandshake(this.request, this.response, wsHandler, attributes);

		assertEquals(1, attributes.size());
		assertEquals("123", attributes.get(HttpSessionHandshakeInterceptor.HTTP_SESSION_ID_ATTR_NAME));
	}

	@Test
	public void doNotCauseSessionCreation() throws Exception {
		Map<String, Object> attributes = new HashMap<>();
		WebSocketHandler wsHandler = Mockito.mock(WebSocketHandler.class);

		HttpSessionHandshakeInterceptor interceptor = new HttpSessionHandshakeInterceptor();
		interceptor.beforeHandshake(this.request, this.response, wsHandler, attributes);

		assertNull(this.servletRequest.getSession(false));
	}

}
