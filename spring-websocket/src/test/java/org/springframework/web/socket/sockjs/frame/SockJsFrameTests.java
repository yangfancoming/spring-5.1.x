

package org.springframework.web.socket.sockjs.frame;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link org.springframework.web.socket.sockjs.frame.SockJsFrame}.
 *
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public class SockJsFrameTests {


	@Test
	public void openFrame() {
		SockJsFrame frame = SockJsFrame.openFrame();

		assertEquals("o", frame.getContent());
		assertEquals(SockJsFrameType.OPEN, frame.getType());
		assertNull(frame.getFrameData());
	}

	@Test
	public void heartbeatFrame() {
		SockJsFrame frame = SockJsFrame.heartbeatFrame();

		assertEquals("h", frame.getContent());
		assertEquals(SockJsFrameType.HEARTBEAT, frame.getType());
		assertNull(frame.getFrameData());
	}

	@Test
	public void messageArrayFrame() {
		SockJsFrame frame = SockJsFrame.messageFrame(new Jackson2SockJsMessageCodec(), "m1", "m2");

		assertEquals("a[\"m1\",\"m2\"]", frame.getContent());
		assertEquals(SockJsFrameType.MESSAGE, frame.getType());
		assertEquals("[\"m1\",\"m2\"]", frame.getFrameData());
	}

	@Test
	public void messageArrayFrameEmpty() {
		SockJsFrame frame = new SockJsFrame("a");

		assertEquals("a[]", frame.getContent());
		assertEquals(SockJsFrameType.MESSAGE, frame.getType());
		assertEquals("[]", frame.getFrameData());

		frame = new SockJsFrame("a[]");

		assertEquals("a[]", frame.getContent());
		assertEquals(SockJsFrameType.MESSAGE, frame.getType());
		assertEquals("[]", frame.getFrameData());
	}

	@Test
	public void closeFrame() {
		SockJsFrame frame = SockJsFrame.closeFrame(3000, "Go Away!");

		assertEquals("c[3000,\"Go Away!\"]", frame.getContent());
		assertEquals(SockJsFrameType.CLOSE, frame.getType());
		assertEquals("[3000,\"Go Away!\"]", frame.getFrameData());
	}

	@Test
	public void closeFrameEmpty() {
		SockJsFrame frame = new SockJsFrame("c");

		assertEquals("c[]", frame.getContent());
		assertEquals(SockJsFrameType.CLOSE, frame.getType());
		assertEquals("[]", frame.getFrameData());

		frame = new SockJsFrame("c[]");

		assertEquals("c[]", frame.getContent());
		assertEquals(SockJsFrameType.CLOSE, frame.getType());
		assertEquals("[]", frame.getFrameData());
	}

}
