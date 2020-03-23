

package org.springframework.web.reactive.socket.server.upgrade;

import java.io.IOException;
import java.util.Collections;
import java.util.function.Supplier;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Endpoint;
import javax.websocket.server.ServerContainer;

import org.apache.tomcat.websocket.server.WsServerContainer;
import reactor.core.publisher.Mono;

import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.server.reactive.AbstractServerHttpRequest;
import org.springframework.http.server.reactive.AbstractServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.adapter.StandardWebSocketHandlerAdapter;
import org.springframework.web.reactive.socket.adapter.TomcatWebSocketSession;
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy;
import org.springframework.web.server.ServerWebExchange;

/**
 * A {@link RequestUpgradeStrategy} for use with Tomcat.
 *
 * @author Violeta Georgieva
 * @author Rossen Stoyanchev
 * @since 5.0
 */
public class TomcatRequestUpgradeStrategy implements RequestUpgradeStrategy {

	private static final String SERVER_CONTAINER_ATTR = "javax.websocket.server.ServerContainer";


	@Nullable
	private Long asyncSendTimeout;

	@Nullable
	private Long maxSessionIdleTimeout;

	@Nullable
	private Integer maxTextMessageBufferSize;

	@Nullable
	private Integer maxBinaryMessageBufferSize;

	@Nullable
	private WsServerContainer serverContainer;


	/**
	 * Exposes the underlying config option on
	 * {@link javax.websocket.server.ServerContainer#setAsyncSendTimeout(long)}.
	 */
	public void setAsyncSendTimeout(Long timeoutInMillis) {
		this.asyncSendTimeout = timeoutInMillis;
	}

	@Nullable
	public Long getAsyncSendTimeout() {
		return this.asyncSendTimeout;
	}

	/**
	 * Exposes the underlying config option on
	 * {@link javax.websocket.server.ServerContainer#setDefaultMaxSessionIdleTimeout(long)}.
	 */
	public void setMaxSessionIdleTimeout(Long timeoutInMillis) {
		this.maxSessionIdleTimeout = timeoutInMillis;
	}

	@Nullable
	public Long getMaxSessionIdleTimeout() {
		return this.maxSessionIdleTimeout;
	}

	/**
	 * Exposes the underlying config option on
	 * {@link javax.websocket.server.ServerContainer#setDefaultMaxTextMessageBufferSize(int)}.
	 */
	public void setMaxTextMessageBufferSize(Integer bufferSize) {
		this.maxTextMessageBufferSize = bufferSize;
	}

	@Nullable
	public Integer getMaxTextMessageBufferSize() {
		return this.maxTextMessageBufferSize;
	}

	/**
	 * Exposes the underlying config option on
	 * {@link javax.websocket.server.ServerContainer#setDefaultMaxBinaryMessageBufferSize(int)}.
	 */
	public void setMaxBinaryMessageBufferSize(Integer bufferSize) {
		this.maxBinaryMessageBufferSize = bufferSize;
	}

	@Nullable
	public Integer getMaxBinaryMessageBufferSize() {
		return this.maxBinaryMessageBufferSize;
	}


	@Override
	public Mono<Void> upgrade(ServerWebExchange exchange, WebSocketHandler handler,
			@Nullable String subProtocol, Supplier<HandshakeInfo> handshakeInfoFactory){

		ServerHttpRequest request = exchange.getRequest();
		ServerHttpResponse response = exchange.getResponse();

		HttpServletRequest servletRequest = getHttpServletRequest(request);
		HttpServletResponse servletResponse = getHttpServletResponse(response);

		HandshakeInfo handshakeInfo = handshakeInfoFactory.get();
		DataBufferFactory bufferFactory = response.bufferFactory();

		Endpoint endpoint = new StandardWebSocketHandlerAdapter(
				handler, session -> new TomcatWebSocketSession(session, handshakeInfo, bufferFactory));

		String requestURI = servletRequest.getRequestURI();
		DefaultServerEndpointConfig config = new DefaultServerEndpointConfig(requestURI, endpoint);
		config.setSubprotocols(subProtocol != null ?
				Collections.singletonList(subProtocol) : Collections.emptyList());

		try {
			WsServerContainer container = getContainer(servletRequest);
			container.doUpgrade(servletRequest, servletResponse, config, Collections.emptyMap());
		}
		catch (ServletException | IOException ex) {
			return Mono.error(ex);
		}

		return Mono.empty();
	}

	private HttpServletRequest getHttpServletRequest(ServerHttpRequest request) {
		Assert.isInstanceOf(AbstractServerHttpRequest.class, request, "ServletServerHttpRequest required");
		return ((AbstractServerHttpRequest) request).getNativeRequest();
	}

	private HttpServletResponse getHttpServletResponse(ServerHttpResponse response) {
		Assert.isInstanceOf(AbstractServerHttpResponse.class, response, "ServletServerHttpResponse required");
		return ((AbstractServerHttpResponse) response).getNativeResponse();
	}

	private WsServerContainer getContainer(HttpServletRequest request) {
		if (this.serverContainer == null) {
			Object container = request.getServletContext().getAttribute(SERVER_CONTAINER_ATTR);
			Assert.state(container instanceof WsServerContainer,
					"ServletContext attribute 'javax.websocket.server.ServerContainer' not found.");
			this.serverContainer = (WsServerContainer) container;
			initServerContainer(this.serverContainer);
		}
		return this.serverContainer;
	}

	private void initServerContainer(ServerContainer serverContainer) {
		if (this.asyncSendTimeout != null) {
			serverContainer.setAsyncSendTimeout(this.asyncSendTimeout);
		}
		if (this.maxSessionIdleTimeout != null) {
			serverContainer.setDefaultMaxSessionIdleTimeout(this.maxSessionIdleTimeout);
		}
		if (this.maxTextMessageBufferSize != null) {
			serverContainer.setDefaultMaxTextMessageBufferSize(this.maxTextMessageBufferSize);
		}
		if (this.maxBinaryMessageBufferSize != null) {
			serverContainer.setDefaultMaxBinaryMessageBufferSize(this.maxBinaryMessageBufferSize);
		}
	}

}
