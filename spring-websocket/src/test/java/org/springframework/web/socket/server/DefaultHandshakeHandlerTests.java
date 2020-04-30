

package org.springframework.web.socket.server;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.web.socket.AbstractHttpRequestTests;
import org.springframework.web.socket.SubProtocolCapable;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import static org.mockito.BDDMockito.*;

/**
 * Test fixture for {@link org.springframework.web.socket.server.support.DefaultHandshakeHandler}.
 *
 *
 */
public class DefaultHandshakeHandlerTests extends AbstractHttpRequestTests {

	private DefaultHandshakeHandler handshakeHandler;

	@Mock
	private RequestUpgradeStrategy upgradeStrategy;


	@Before
	public void setup() {
		super.setup();

		MockitoAnnotations.initMocks(this);
		this.handshakeHandler = new DefaultHandshakeHandler(this.upgradeStrategy);
	}


	@Test
	public void supportedSubProtocols() {
		this.handshakeHandler.setSupportedProtocols("stomp", "mqtt");
		given(this.upgradeStrategy.getSupportedVersions()).willReturn(new String[] {"13"});
		this.servletRequest.setMethod("GET");

		WebSocketHttpHeaders headers = new WebSocketHttpHeaders(this.request.getHeaders());
		headers.setUpgrade("WebSocket");
		headers.setConnection("Upgrade");
		headers.setSecWebSocketVersion("13");
		headers.setSecWebSocketKey("82/ZS2YHjEnUN97HLL8tbw==");
		headers.setSecWebSocketProtocol("STOMP");

		WebSocketHandler handler = new TextWebSocketHandler();
		Map<String, Object> attributes = Collections.emptyMap();
		this.handshakeHandler.doHandshake(this.request, this.response, handler, attributes);

		verify(this.upgradeStrategy).upgrade(this.request, this.response, "STOMP",
				Collections.emptyList(), null, handler, attributes);
	}

	@Test
	public void supportedExtensions() {
		WebSocketExtension extension1 = new WebSocketExtension("ext1");
		WebSocketExtension extension2 = new WebSocketExtension("ext2");

		given(this.upgradeStrategy.getSupportedVersions()).willReturn(new String[] {"13"});
		given(this.upgradeStrategy.getSupportedExtensions(this.request)).willReturn(Collections.singletonList(extension1));

		this.servletRequest.setMethod("GET");

		WebSocketHttpHeaders headers = new WebSocketHttpHeaders(this.request.getHeaders());
		headers.setUpgrade("WebSocket");
		headers.setConnection("Upgrade");
		headers.setSecWebSocketVersion("13");
		headers.setSecWebSocketKey("82/ZS2YHjEnUN97HLL8tbw==");
		headers.setSecWebSocketExtensions(Arrays.asList(extension1, extension2));

		WebSocketHandler handler = new TextWebSocketHandler();
		Map<String, Object> attributes = Collections.<String, Object>emptyMap();
		this.handshakeHandler.doHandshake(this.request, this.response, handler, attributes);

		verify(this.upgradeStrategy).upgrade(this.request, this.response, null,
				Collections.singletonList(extension1), null, handler, attributes);
	}

	@Test
	public void subProtocolCapableHandler() {
		given(this.upgradeStrategy.getSupportedVersions()).willReturn(new String[] {"13"});

		this.servletRequest.setMethod("GET");

		WebSocketHttpHeaders headers = new WebSocketHttpHeaders(this.request.getHeaders());
		headers.setUpgrade("WebSocket");
		headers.setConnection("Upgrade");
		headers.setSecWebSocketVersion("13");
		headers.setSecWebSocketKey("82/ZS2YHjEnUN97HLL8tbw==");
		headers.setSecWebSocketProtocol("v11.stomp");

		WebSocketHandler handler = new SubProtocolCapableHandler("v12.stomp", "v11.stomp");
		Map<String, Object> attributes = Collections.<String, Object>emptyMap();
		this.handshakeHandler.doHandshake(this.request, this.response, handler, attributes);

		verify(this.upgradeStrategy).upgrade(this.request, this.response, "v11.stomp",
				Collections.emptyList(), null, handler, attributes);
	}

	@Test
	public void subProtocolCapableHandlerNoMatch() {
		given(this.upgradeStrategy.getSupportedVersions()).willReturn(new String[] {"13"});

		this.servletRequest.setMethod("GET");

		WebSocketHttpHeaders headers = new WebSocketHttpHeaders(this.request.getHeaders());
		headers.setUpgrade("WebSocket");
		headers.setConnection("Upgrade");
		headers.setSecWebSocketVersion("13");
		headers.setSecWebSocketKey("82/ZS2YHjEnUN97HLL8tbw==");
		headers.setSecWebSocketProtocol("v10.stomp");

		WebSocketHandler handler = new SubProtocolCapableHandler("v12.stomp", "v11.stomp");
		Map<String, Object> attributes = Collections.<String, Object>emptyMap();
		this.handshakeHandler.doHandshake(this.request, this.response, handler, attributes);

		verify(this.upgradeStrategy).upgrade(this.request, this.response, null,
				Collections.emptyList(), null, handler, attributes);
	}


	private static class SubProtocolCapableHandler extends TextWebSocketHandler implements SubProtocolCapable {

		private final List<String> subProtocols;

		public SubProtocolCapableHandler(String... subProtocols) {
			this.subProtocols = Arrays.asList(subProtocols);
		}

		@Override
		public List<String> getSubProtocols() {
			return this.subProtocols;
		}
	}

}
