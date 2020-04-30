

package org.springframework.web.socket.sockjs.transport.session;

import java.io.IOException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.sockjs.frame.DefaultSockJsFrameFormat;
import org.springframework.web.socket.sockjs.frame.SockJsFrame;
import org.springframework.web.socket.sockjs.frame.SockJsFrameFormat;
import org.springframework.web.socket.sockjs.transport.SockJsServiceConfig;
import org.springframework.web.socket.sockjs.transport.session.HttpSockJsSessionTests.TestAbstractHttpSockJsSession;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AbstractHttpSockJsSession}.
 *
 *
 */
public class HttpSockJsSessionTests extends AbstractSockJsSessionTests<TestAbstractHttpSockJsSession> {

	protected ServerHttpRequest request;

	protected ServerHttpResponse response;

	protected MockHttpServletRequest servletRequest;

	protected MockHttpServletResponse servletResponse;

	private SockJsFrameFormat frameFormat;


	@Override
	protected TestAbstractHttpSockJsSession initSockJsSession() {
		return new TestAbstractHttpSockJsSession(this.sockJsConfig, this.webSocketHandler, null);
	}

	@Before
	public void setup() {

		super.setUp();

		this.frameFormat = new DefaultSockJsFrameFormat("%s");

		this.servletResponse = new MockHttpServletResponse();
		this.response = new ServletServerHttpResponse(this.servletResponse);

		this.servletRequest = new MockHttpServletRequest();
		this.servletRequest.setAsyncSupported(true);
		this.request = new ServletServerHttpRequest(this.servletRequest);
	}

	@Test
	public void handleInitialRequest() throws Exception {

		this.session.handleInitialRequest(this.request, this.response, this.frameFormat);

		assertEquals("hhh\no", this.servletResponse.getContentAsString());
		assertTrue(this.servletRequest.isAsyncStarted());

		verify(this.webSocketHandler).afterConnectionEstablished(this.session);
	}

	@Test
	public void handleSuccessiveRequest() throws Exception {

		this.session.getMessageCache().add("x");
		this.session.handleSuccessiveRequest(this.request, this.response, this.frameFormat);

		assertTrue(this.servletRequest.isAsyncStarted());
		assertTrue(this.session.wasHeartbeatScheduled());
		assertTrue(this.session.wasCacheFlushed());
		assertEquals("hhh\n", this.servletResponse.getContentAsString());

		verifyNoMoreInteractions(this.webSocketHandler);
	}


	static class TestAbstractHttpSockJsSession extends StreamingSockJsSession {

		private IOException exceptionOnWriteFrame;

		private boolean cacheFlushed;

		private boolean heartbeatScheduled;


		public TestAbstractHttpSockJsSession(SockJsServiceConfig config, WebSocketHandler handler,
				Map<String, Object> attributes) {

			super("1", config, handler, attributes);
		}

		@Override
		protected byte[] getPrelude(ServerHttpRequest request) {
			return "hhh\n".getBytes();
		}

		public boolean wasCacheFlushed() {
			return this.cacheFlushed;
		}

		public boolean wasHeartbeatScheduled() {
			return this.heartbeatScheduled;
		}

		public void setExceptionOnWriteFrame(IOException exceptionOnWriteFrame) {
			this.exceptionOnWriteFrame = exceptionOnWriteFrame;
		}

		@Override
		protected void flushCache() {
			this.cacheFlushed = true;
			scheduleHeartbeat();
		}

		@Override
		protected void scheduleHeartbeat() {
			this.heartbeatScheduled = true;
		}

		@Override
		protected synchronized void writeFrameInternal(SockJsFrame frame) throws IOException {
			if (this.exceptionOnWriteFrame != null) {
				throw this.exceptionOnWriteFrame;
			}
			else {
				super.writeFrameInternal(frame);
			}
		}
	}

}
