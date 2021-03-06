

package org.springframework.web.socket.sockjs.transport.session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.sockjs.SockJsTransportFailureException;
import org.springframework.web.socket.sockjs.frame.SockJsFrame;
import org.springframework.web.socket.sockjs.transport.SockJsServiceConfig;

/**
 *
 */
public class TestHttpSockJsSession extends StreamingSockJsSession {

	private boolean active;

	private final List<SockJsFrame> sockJsFrames = new ArrayList<>();

	private CloseStatus closeStatus;

	private IOException exceptionOnWrite;

	private int numberOfLastActiveTimeUpdates;

	private boolean cancelledHeartbeat;

	private String subProtocol;


	public TestHttpSockJsSession(String sessionId, SockJsServiceConfig config,
			WebSocketHandler wsHandler, Map<String, Object> attributes) {

		super(sessionId, config, wsHandler, attributes);
	}

	@Override
	protected byte[] getPrelude(ServerHttpRequest request) {
		return new byte[0];
	}

	@Override
	public String getAcceptedProtocol() {
		return this.subProtocol;
	}

	@Override
	public void setAcceptedProtocol(String protocol) {
		this.subProtocol = protocol;
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
	protected void writeFrameInternal(SockJsFrame frame) throws IOException {
		this.sockJsFrames.add(frame);
		if (this.exceptionOnWrite != null) {
			throw this.exceptionOnWrite;
		}
	}

	@Override
	protected void disconnect(CloseStatus status) {
		this.closeStatus = status;
	}

	@Override
	protected void flushCache() throws SockJsTransportFailureException {
	}

}
