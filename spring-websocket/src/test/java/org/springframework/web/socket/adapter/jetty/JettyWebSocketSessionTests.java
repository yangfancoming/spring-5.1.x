

package org.springframework.web.socket.adapter.jetty;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;

import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.web.socket.handler.TestPrincipal;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

/**
 * Unit tests for {@link org.springframework.web.socket.adapter.jetty.JettyWebSocketSession}.
 *
 *
 */
public class JettyWebSocketSessionTests {

	private final Map<String, Object> attributes = new HashMap<>();


	@Test
	@SuppressWarnings("resource")
	public void getPrincipalWithConstructorArg() {
		TestPrincipal user = new TestPrincipal("joe");
		JettyWebSocketSession session = new JettyWebSocketSession(attributes, user);

		assertSame(user, session.getPrincipal());
	}

	@Test
	@SuppressWarnings("resource")
	public void getPrincipalFromNativeSession() {
		TestPrincipal user = new TestPrincipal("joe");

		UpgradeRequest request = Mockito.mock(UpgradeRequest.class);
		given(request.getUserPrincipal()).willReturn(user);

		UpgradeResponse response = Mockito.mock(UpgradeResponse.class);
		given(response.getAcceptedSubProtocol()).willReturn(null);

		Session nativeSession = Mockito.mock(Session.class);
		given(nativeSession.getUpgradeRequest()).willReturn(request);
		given(nativeSession.getUpgradeResponse()).willReturn(response);

		JettyWebSocketSession session = new JettyWebSocketSession(attributes);
		session.initializeNativeSession(nativeSession);

		reset(nativeSession);

		assertSame(user, session.getPrincipal());
		verifyNoMoreInteractions(nativeSession);
	}

	@Test
	@SuppressWarnings("resource")
	public void getPrincipalNotAvailable() {
		UpgradeRequest request = Mockito.mock(UpgradeRequest.class);
		given(request.getUserPrincipal()).willReturn(null);

		UpgradeResponse response = Mockito.mock(UpgradeResponse.class);
		given(response.getAcceptedSubProtocol()).willReturn(null);

		Session nativeSession = Mockito.mock(Session.class);
		given(nativeSession.getUpgradeRequest()).willReturn(request);
		given(nativeSession.getUpgradeResponse()).willReturn(response);

		JettyWebSocketSession session = new JettyWebSocketSession(attributes);
		session.initializeNativeSession(nativeSession);

		reset(nativeSession);

		assertNull(session.getPrincipal());
		verifyNoMoreInteractions(nativeSession);
	}

	@Test
	@SuppressWarnings("resource")
	public void getAcceptedProtocol() {
		String protocol = "foo";

		UpgradeRequest request = Mockito.mock(UpgradeRequest.class);
		given(request.getUserPrincipal()).willReturn(null);

		UpgradeResponse response = Mockito.mock(UpgradeResponse.class);
		given(response.getAcceptedSubProtocol()).willReturn(protocol);

		Session nativeSession = Mockito.mock(Session.class);
		given(nativeSession.getUpgradeRequest()).willReturn(request);
		given(nativeSession.getUpgradeResponse()).willReturn(response);

		JettyWebSocketSession session = new JettyWebSocketSession(attributes);
		session.initializeNativeSession(nativeSession);

		reset(nativeSession);

		assertSame(protocol, session.getAcceptedProtocol());
		verifyNoMoreInteractions(nativeSession);
	}

}
