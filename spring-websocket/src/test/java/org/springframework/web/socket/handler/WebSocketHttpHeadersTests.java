

package org.springframework.web.socket.handler;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketHttpHeaders;

import static org.junit.Assert.*;

/**
 * Unit tests for WebSocketHttpHeaders.
 *
 * @author Rossen Stoyanchev
 */
public class WebSocketHttpHeadersTests {

	private WebSocketHttpHeaders headers;

	@Before
	public void setUp() {
		headers = new WebSocketHttpHeaders();
	}

	@Test
	public void parseWebSocketExtensions() {
		List<String> extensions = new ArrayList<>();
		extensions.add("x-foo-extension, x-bar-extension");
		extensions.add("x-test-extension");
		this.headers.put(WebSocketHttpHeaders.SEC_WEBSOCKET_EXTENSIONS, extensions);

		List<WebSocketExtension> parsedExtensions = this.headers.getSecWebSocketExtensions();
		assertThat(parsedExtensions, Matchers.hasSize(3));
	}

}
