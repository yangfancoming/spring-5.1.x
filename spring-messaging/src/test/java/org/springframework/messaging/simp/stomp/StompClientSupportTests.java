

package org.springframework.messaging.simp.stomp;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link StompClientSupport}.
 *
 * @author Rossen Stoyanchev
 */
public class StompClientSupportTests {

	private final StompClientSupport stompClient = new StompClientSupport() {};


	@Test
	public void defaultHeartbeatValidation() throws Exception {
		trySetDefaultHeartbeat(new long[] {-1, 0});
		trySetDefaultHeartbeat(new long[] {0, -1});
	}

	private void trySetDefaultHeartbeat(long[] heartbeat) {
		try {
			this.stompClient.setDefaultHeartbeat(heartbeat);
			fail("Expected exception");
		}
		catch (IllegalArgumentException ex) {
			// Ignore
		}
	}

	@Test
	public void defaultHeartbeatValue() throws Exception {
		assertArrayEquals(new long[] {10000, 10000}, this.stompClient.getDefaultHeartbeat());
	}

	@Test
	public void isDefaultHeartbeatEnabled() throws Exception {
		assertArrayEquals(new long[] {10000, 10000}, this.stompClient.getDefaultHeartbeat());
		assertTrue(this.stompClient.isDefaultHeartbeatEnabled());

		this.stompClient.setDefaultHeartbeat(new long[] {0, 0});
		assertFalse(this.stompClient.isDefaultHeartbeatEnabled());
	}

	@Test
	public void processConnectHeadersDefault() throws Exception {
		StompHeaders connectHeaders = this.stompClient.processConnectHeaders(null);

		assertNotNull(connectHeaders);
		assertArrayEquals(new long[] {10000, 10000}, connectHeaders.getHeartbeat());
	}

	@Test
	public void processConnectHeadersWithExplicitHeartbeat() throws Exception {

		StompHeaders connectHeaders = new StompHeaders();
		connectHeaders.setHeartbeat(new long[] {15000, 15000});
		connectHeaders = this.stompClient.processConnectHeaders(connectHeaders);

		assertNotNull(connectHeaders);
		assertArrayEquals(new long[] {15000, 15000}, connectHeaders.getHeartbeat());
	}

}
