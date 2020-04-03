

package org.springframework.web.socket.server.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import static org.junit.Assert.*;
import org.junit.Test;
import org.mockito.Mockito;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.socket.AbstractHttpRequestTests;
import org.springframework.web.socket.WebSocketHandler;

/**
 * Test fixture for {@link OriginHandshakeInterceptor}.
 *
 * @author Sebastien Deleuze
 */
public class OriginHandshakeInterceptorTests extends AbstractHttpRequestTests {

	@Test(expected = IllegalArgumentException.class)
	public void invalidInput() {
		new OriginHandshakeInterceptor(null);
	}

	@Test
	public void originValueMatch() throws Exception {
		Map<String, Object> attributes = new HashMap<>();
		WebSocketHandler wsHandler = Mockito.mock(WebSocketHandler.class);
		this.servletRequest.addHeader(HttpHeaders.ORIGIN, "https://mydomain1.com");
		List<String> allowed = Collections.singletonList("https://mydomain1.com");
		OriginHandshakeInterceptor interceptor = new OriginHandshakeInterceptor(allowed);
		assertTrue(interceptor.beforeHandshake(request, response, wsHandler, attributes));
		assertNotEquals(servletResponse.getStatus(), HttpStatus.FORBIDDEN.value());
	}

	@Test
	public void originValueNoMatch() throws Exception {
		Map<String, Object> attributes = new HashMap<>();
		WebSocketHandler wsHandler = Mockito.mock(WebSocketHandler.class);
		this.servletRequest.addHeader(HttpHeaders.ORIGIN, "https://mydomain1.com");
		List<String> allowed = Collections.singletonList("http://mydomain2.com");
		OriginHandshakeInterceptor interceptor = new OriginHandshakeInterceptor(allowed);
		assertFalse(interceptor.beforeHandshake(request, response, wsHandler, attributes));
		assertEquals(servletResponse.getStatus(), HttpStatus.FORBIDDEN.value());
	}

	@Test
	public void originListMatch() throws Exception {
		Map<String, Object> attributes = new HashMap<>();
		WebSocketHandler wsHandler = Mockito.mock(WebSocketHandler.class);
		this.servletRequest.addHeader(HttpHeaders.ORIGIN, "http://mydomain2.com");
		List<String> allowed = Arrays.asList("https://mydomain1.com", "http://mydomain2.com", "http://mydomain3.com");
		OriginHandshakeInterceptor interceptor = new OriginHandshakeInterceptor(allowed);
		assertTrue(interceptor.beforeHandshake(request, response, wsHandler, attributes));
		assertNotEquals(servletResponse.getStatus(), HttpStatus.FORBIDDEN.value());
	}

	@Test
	public void originListNoMatch() throws Exception {
		Map<String, Object> attributes = new HashMap<>();
		WebSocketHandler wsHandler = Mockito.mock(WebSocketHandler.class);
		this.servletRequest.addHeader(HttpHeaders.ORIGIN, "http://www.mydomain4.com/");
		List<String> allowed = Arrays.asList("https://mydomain1.com", "http://mydomain2.com", "http://mydomain3.com");
		OriginHandshakeInterceptor interceptor = new OriginHandshakeInterceptor(allowed);
		assertFalse(interceptor.beforeHandshake(request, response, wsHandler, attributes));
		assertEquals(servletResponse.getStatus(), HttpStatus.FORBIDDEN.value());
	}

	@Test
	public void originNoMatchWithNullHostileCollection() throws Exception {
		Map<String, Object> attributes = new HashMap<>();
		WebSocketHandler wsHandler = Mockito.mock(WebSocketHandler.class);
		this.servletRequest.addHeader(HttpHeaders.ORIGIN, "http://www.mydomain4.com/");
		OriginHandshakeInterceptor interceptor = new OriginHandshakeInterceptor();
		Set<String> allowedOrigins = new ConcurrentSkipListSet<>();
		allowedOrigins.add("https://mydomain1.com");
		interceptor.setAllowedOrigins(allowedOrigins);
		assertFalse(interceptor.beforeHandshake(request, response, wsHandler, attributes));
		assertEquals(servletResponse.getStatus(), HttpStatus.FORBIDDEN.value());
	}

	@Test
	public void originMatchAll() throws Exception {
		Map<String, Object> attributes = new HashMap<>();
		WebSocketHandler wsHandler = Mockito.mock(WebSocketHandler.class);
		this.servletRequest.addHeader(HttpHeaders.ORIGIN, "https://mydomain1.com");
		OriginHandshakeInterceptor interceptor = new OriginHandshakeInterceptor();
		interceptor.setAllowedOrigins(Collections.singletonList("*"));
		assertTrue(interceptor.beforeHandshake(request, response, wsHandler, attributes));
		assertNotEquals(servletResponse.getStatus(), HttpStatus.FORBIDDEN.value());
	}

	@Test
	public void sameOriginMatchWithEmptyAllowedOrigins() throws Exception {
		Map<String, Object> attributes = new HashMap<>();
		WebSocketHandler wsHandler = Mockito.mock(WebSocketHandler.class);
		this.servletRequest.addHeader(HttpHeaders.ORIGIN, "http://mydomain2.com");
		this.servletRequest.setServerName("mydomain2.com");
		OriginHandshakeInterceptor interceptor = new OriginHandshakeInterceptor(Collections.emptyList());
		assertTrue(interceptor.beforeHandshake(request, response, wsHandler, attributes));
		assertNotEquals(servletResponse.getStatus(), HttpStatus.FORBIDDEN.value());
	}

	@Test
	public void sameOriginMatchWithAllowedOrigins() throws Exception {
		Map<String, Object> attributes = new HashMap<>();
		WebSocketHandler wsHandler = Mockito.mock(WebSocketHandler.class);
		this.servletRequest.addHeader(HttpHeaders.ORIGIN, "http://mydomain2.com");
		this.servletRequest.setServerName("mydomain2.com");
		OriginHandshakeInterceptor interceptor = new OriginHandshakeInterceptor(Arrays.asList("https://mydomain1.com"));
		assertTrue(interceptor.beforeHandshake(request, response, wsHandler, attributes));
		assertNotEquals(servletResponse.getStatus(), HttpStatus.FORBIDDEN.value());
	}

	@Test
	public void sameOriginNoMatch() throws Exception {
		Map<String, Object> attributes = new HashMap<>();
		WebSocketHandler wsHandler = Mockito.mock(WebSocketHandler.class);
		this.servletRequest.addHeader(HttpHeaders.ORIGIN, "http://mydomain3.com");
		this.servletRequest.setServerName("mydomain2.com");
		OriginHandshakeInterceptor interceptor = new OriginHandshakeInterceptor(Collections.emptyList());
		assertFalse(interceptor.beforeHandshake(request, response, wsHandler, attributes));
		assertEquals(servletResponse.getStatus(), HttpStatus.FORBIDDEN.value());
	}

}
