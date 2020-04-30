

package org.springframework.web.socket.server.standard;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Endpoint;
import javax.websocket.Extension;

import io.undertow.websockets.core.WebSocketVersion;
import io.undertow.websockets.jsr.ServerWebSocketContainer;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.server.HandshakeFailureException;

/**
 * A WebSocket {@code RequestUpgradeStrategy} for WildFly and its underlying
 * Undertow web server. Also compatible with embedded Undertow usage.
 *
 * xmlBeanDefinitionReaderRequires Undertow 1.3.5+ as of Spring Framework 5.0.
 *
 *
 * @since 4.0.1
 */
public class UndertowRequestUpgradeStrategy extends AbstractStandardUpgradeStrategy {

	private static final String[] VERSIONS = new String[] {
			WebSocketVersion.V13.toHttpHeaderValue(),
			WebSocketVersion.V08.toHttpHeaderValue(),
			WebSocketVersion.V07.toHttpHeaderValue()
	};


	@Override
	public String[] getSupportedVersions() {
		return VERSIONS;
	}

	@Override
	protected void upgradeInternal(ServerHttpRequest request, ServerHttpResponse response,
			@Nullable String selectedProtocol, List<Extension> selectedExtensions, Endpoint endpoint)
			throws HandshakeFailureException {

		HttpServletRequest servletRequest = getHttpServletRequest(request);
		HttpServletResponse servletResponse = getHttpServletResponse(response);

		StringBuffer requestUrl = servletRequest.getRequestURL();
		String path = servletRequest.getRequestURI();  // shouldn't matter
		Map<String, String> pathParams = Collections.emptyMap();

		ServerEndpointRegistration endpointConfig = new ServerEndpointRegistration(path, endpoint);
		endpointConfig.setSubprotocols(Collections.singletonList(selectedProtocol));
		endpointConfig.setExtensions(selectedExtensions);

		try {
			getContainer(servletRequest).doUpgrade(servletRequest, servletResponse, endpointConfig, pathParams);
		}
		catch (ServletException ex) {
			throw new HandshakeFailureException(
					"Servlet request failed to upgrade to WebSocket: " + requestUrl, ex);
		}
		catch (IOException ex) {
			throw new HandshakeFailureException(
					"Response update failed during upgrade to WebSocket: " + requestUrl, ex);
		}
	}

	@Override
	public ServerWebSocketContainer getContainer(HttpServletRequest request) {
		return (ServerWebSocketContainer) super.getContainer(request);
	}

}
