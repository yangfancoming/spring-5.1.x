

package org.springframework.web.socket.sockjs.transport.session;

import org.junit.Before;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.WebSocketHandler;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Base class for SockJS Session tests classes.
 *
 *
 */
public abstract class AbstractSockJsSessionTests<S extends AbstractSockJsSession> {

	protected WebSocketHandler webSocketHandler;

	protected StubSockJsServiceConfig sockJsConfig;

	protected TaskScheduler taskScheduler;

	protected S session;


	@Before
	public void setUp() {
		this.webSocketHandler = mock(WebSocketHandler.class);
		this.taskScheduler = mock(TaskScheduler.class);

		this.sockJsConfig = new StubSockJsServiceConfig();
		this.sockJsConfig.setTaskScheduler(this.taskScheduler);

		this.session = initSockJsSession();
	}

	protected abstract S initSockJsSession();

	protected void assertNew() {
		assertState(true, false, false);
	}

	protected void assertOpen() {
		assertState(false, true, false);
	}

	protected void assertClosed() {
		assertState(false, false, true);
	}

	private void assertState(boolean isNew, boolean isOpen, boolean isClosed) {
		assertEquals(isNew, this.session.isNew());
		assertEquals(isOpen, this.session.isOpen());
		assertEquals(isClosed, this.session.isClosed());
	}

}
