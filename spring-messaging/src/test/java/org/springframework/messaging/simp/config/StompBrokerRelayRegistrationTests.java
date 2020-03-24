

package org.springframework.messaging.simp.config;

import org.junit.Test;

import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.StubMessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.simp.stomp.StompBrokerRelayMessageHandler;
import org.springframework.util.StringUtils;

import static org.junit.Assert.*;

/**
 * Unit tests for
 * {@link org.springframework.messaging.simp.config.StompBrokerRelayRegistration}.
 *
 * @author Rossen Stoyanchev
 */
public class StompBrokerRelayRegistrationTests {

	@Test
	public void test() {

		SubscribableChannel inChannel = new StubMessageChannel();
		MessageChannel outChannel = new StubMessageChannel();
		String[] prefixes = new String[] { "/foo", "/bar" };

		StompBrokerRelayRegistration registration = new StompBrokerRelayRegistration(inChannel, outChannel, prefixes);
		registration.setClientLogin("clientlogin");
		registration.setClientPasscode("clientpasscode");
		registration.setSystemLogin("syslogin");
		registration.setSystemPasscode("syspasscode");
		registration.setSystemHeartbeatReceiveInterval(123);
		registration.setSystemHeartbeatSendInterval(456);
		registration.setVirtualHost("example.org");

		StompBrokerRelayMessageHandler handler = registration.getMessageHandler(new StubMessageChannel());

		assertArrayEquals(prefixes, StringUtils.toStringArray(handler.getDestinationPrefixes()));
		assertEquals("clientlogin", handler.getClientLogin());
		assertEquals("clientpasscode", handler.getClientPasscode());
		assertEquals("syslogin", handler.getSystemLogin());
		assertEquals("syspasscode", handler.getSystemPasscode());
		assertEquals(123, handler.getSystemHeartbeatReceiveInterval());
		assertEquals(456, handler.getSystemHeartbeatSendInterval());
		assertEquals("example.org", handler.getVirtualHost());
	}

}
