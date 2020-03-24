

package org.springframework.messaging.simp;

import java.util.Collections;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for SimpMessageHeaderAccessor.
 *
 * @author Rossen Stoyanchev
 */
public class SimpMessageHeaderAccessorTests {


	@Test
	public void getShortLogMessage() {
		assertEquals("MESSAGE session=null payload=p", SimpMessageHeaderAccessor.create().getShortLogMessage("p"));
	}

	@Test
	public void getLogMessageWithValuesSet() {
		SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();
		accessor.setDestination("/destination");
		accessor.setSubscriptionId("subscription");
		accessor.setSessionId("session");
		accessor.setUser(new TestPrincipal("user"));
		accessor.setSessionAttributes(Collections.<String, Object>singletonMap("key", "value"));

		assertEquals("MESSAGE destination=/destination subscriptionId=subscription " +
				"session=session user=user attributes[1] payload=p", accessor.getShortLogMessage("p"));
	}

	@Test
	public void getDetailedLogMessageWithValuesSet() {
		SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();
		accessor.setDestination("/destination");
		accessor.setSubscriptionId("subscription");
		accessor.setSessionId("session");
		accessor.setUser(new TestPrincipal("user"));
		accessor.setSessionAttributes(Collections.<String, Object>singletonMap("key", "value"));
		accessor.setNativeHeader("nativeKey", "nativeValue");

		assertEquals("MESSAGE destination=/destination subscriptionId=subscription " +
				"session=session user=user attributes={key=value} nativeHeaders=" +
				"{nativeKey=[nativeValue]} payload=p", accessor.getDetailedLogMessage("p"));
	}

}
