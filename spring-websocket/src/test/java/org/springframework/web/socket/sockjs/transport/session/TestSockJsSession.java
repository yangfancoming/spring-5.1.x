

package org.springframework.web.socket.sockjs.transport.session;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.sockjs.frame.SockJsFrame;
import org.springframework.web.socket.sockjs.transport.SockJsServiceConfig;

/**
 *
 */
public class TestSockJsSession extends AbstractSockJsSession {

	private URI uri;

	private HttpHeaders headers;

	private Principal principal;

	private InetSocketAddress localAddress;

	private InetSocketAddress remoteAddress;

	private boolean active;

	private final List<SockJsFrame> sockJsFrames = new ArrayList<>();

	private CloseStatus closeStatus;

	private IOException exceptionOnWrite;

	private int numberOfLastActiveTimeUpdates;

	private boolean cancelledHeartbeat;

	private String subProtocol;

	private List<WebSocketExtension> extensions = new ArrayList<>();


	public TestSockJsSession(String sessionId, SockJsServiceConfig config,
			WebSocketHandler wsHandler, Map<String, Object> attributes) {

		super(sessionId, config, wsHandler, attributes);
	}


	public void setUri(URI uri) {
		this.uri = uri;
	}

	@Override
	public URI getUri() {
		return this.uri;
	}

	@Override
	public HttpHeaders getHandshakeHeaders() {
		return this.headers;
	}

	public HttpHeaders getHeaders() {
		return this.headers;
	}

	public void setHeaders(HttpHeaders headers) {
		this.headers = headers;
	}

	@Override
	public Principal getPrincipal() {
		return this.principal;
	}

	public void setPrincipal(Principal principal) {
		this.principal = principal;
	}

	@Override
	public InetSocketAddress getLocalAddress() {
		return this.localAddress;
	}

	public void setLocalAddress(InetSocketAddress localAddress) {
		this.localAddress = localAddress;
	}

	@Override
	public InetSocketAddress getRemoteAddress() {
		return this.remoteAddress;
	}

	public void setRemoteAddress(InetSocketAddress remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	@Override
	public String getAcceptedProtocol() {
		return this.subProtocol;
	}

	public void setAcceptedProtocol(String protocol) {
		this.subProtocol = protocol;
	}

	@Override
	public void setTextMessageSizeLimit(int messageSizeLimit) {
	}

	@Override
	public int getTextMessageSizeLimit() {
		return 0;
	}

	@Override
	public void setBinaryMessageSizeLimit(int messageSizeLimit) {
	}

	@Override
	public int getBinaryMessageSizeLimit() {
		return 0;
	}

	@Override
	public List<WebSocketExtension> getExtensions() {
		return this.extensions;
	}

	public void setExtensions(List<WebSocketExtension> extensions) {
		this.extensions = extensions;
	}

	public CloseStatus getCloseStatus() {
		return this.closeStatus;
	}

	@Override
	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<SockJsFrame> getSockJsFramesWritten() {
		return this.sockJsFrames;
	}

	public void setExceptionOnWrite(IOException exceptionOnWrite) {
		this.exceptionOnWrite = exceptionOnWrite;
	}

	public int getNumberOfLastActiveTimeUpdates() {
		return this.numberOfLastActiveTimeUpdates;
	}

	public boolean didCancelHeartbeat() {
		return this.cancelledHeartbeat;
	}

	@Override
	protected void updateLastActiveTime() {
		this.numberOfLastActiveTimeUpdates++;
		super.updateLastActiveTime();
	}

	@Override
	protected void cancelHeartbeat() {
		this.cancelledHeartbeat = true;
		super.cancelHeartbeat();
	}

	@Override
	protected void sendMessageInternal(String message) {
	}

	@Override
	protected void writeFrameInternal(SockJsFrame frame) throws IOException {
		this.sockJsFrames.add(frame);
		if (this.exceptionOnWrite != null) {
			throw this.exceptionOnWrite;
		}
	}

	@Override
	protected void disconnect(CloseStatus status) throws IOException {
		this.closeStatus = status;
	}

}
