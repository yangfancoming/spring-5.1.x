

package org.springframework.web.socket.sockjs.transport;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Rossen Stoyanchev
 */
public class TransportTypeTests {

	@Test
	public void testFromValue() {
		assertEquals(TransportType.WEBSOCKET, TransportType.fromValue("websocket"));
		assertEquals(TransportType.XHR, TransportType.fromValue("xhr"));
		assertEquals(TransportType.XHR_SEND, TransportType.fromValue("xhr_send"));
		assertEquals(TransportType.XHR_STREAMING, TransportType.fromValue("xhr_streaming"));
		assertEquals(TransportType.EVENT_SOURCE, TransportType.fromValue("eventsource"));
		assertEquals(TransportType.HTML_FILE, TransportType.fromValue("htmlfile"));
	}

}
