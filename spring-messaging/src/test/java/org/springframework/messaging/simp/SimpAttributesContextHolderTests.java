

package org.springframework.messaging.simp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * Unit tests for
 * {@link org.springframework.messaging.simp.SimpAttributesContextHolder}.
 *
 *
 * @since 4.1
 */
public class SimpAttributesContextHolderTests {

	private SimpAttributes simpAttributes;

	@Rule
	public ExpectedException thrown = ExpectedException.none();


	@Before
	public void setUp() {
		Map<String, Object> map = new ConcurrentHashMap<>();
		this.simpAttributes = new SimpAttributes("session1", map);
	}

	@After
	public void tearDown() {
		SimpAttributesContextHolder.resetAttributes();
	}


	@Test
	public void resetAttributes() {
		SimpAttributesContextHolder.setAttributes(this.simpAttributes);
		assertThat(SimpAttributesContextHolder.getAttributes(), sameInstance(this.simpAttributes));

		SimpAttributesContextHolder.resetAttributes();
		assertThat(SimpAttributesContextHolder.getAttributes(), nullValue());
	}

	@Test
	public void getAttributes() {
		assertThat(SimpAttributesContextHolder.getAttributes(), nullValue());

		SimpAttributesContextHolder.setAttributes(this.simpAttributes);
		assertThat(SimpAttributesContextHolder.getAttributes(), sameInstance(this.simpAttributes));
	}

	@Test
	public void setAttributes() {
		SimpAttributesContextHolder.setAttributes(this.simpAttributes);
		assertThat(SimpAttributesContextHolder.getAttributes(), sameInstance(this.simpAttributes));

		SimpAttributesContextHolder.setAttributes(null);
		assertThat(SimpAttributesContextHolder.getAttributes(), nullValue());
	}

	@Test
	public void setAttributesFromMessage() {

		String sessionId = "session1";
		ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();

		SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
		headerAccessor.setSessionId(sessionId);
		headerAccessor.setSessionAttributes(map);
		Message<?> message = MessageBuilder.createMessage("", headerAccessor.getMessageHeaders());

		SimpAttributesContextHolder.setAttributesFromMessage(message);

		SimpAttributes attrs = SimpAttributesContextHolder.getAttributes();
		assertThat(attrs, notNullValue());
		assertThat(attrs.getSessionId(), is(sessionId));

		attrs.setAttribute("name1", "value1");
		assertThat(map.get("name1"), is("value1"));
	}

	@Test
	public void setAttributesFromMessageWithMissingSessionId() {
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage(startsWith("No session id in"));
		SimpAttributesContextHolder.setAttributesFromMessage(new GenericMessage<Object>(""));
	}

	@Test
	public void setAttributesFromMessageWithMissingSessionAttributes() {
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage(startsWith("No session attributes in"));
		SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
		headerAccessor.setSessionId("session1");
		Message<?> message = MessageBuilder.createMessage("", headerAccessor.getMessageHeaders());
		SimpAttributesContextHolder.setAttributesFromMessage(message);
	}

	@Test
	public void currentAttributes() {
		SimpAttributesContextHolder.setAttributes(this.simpAttributes);
		assertThat(SimpAttributesContextHolder.currentAttributes(), sameInstance(this.simpAttributes));
	}

	@Test
	public void currentAttributesNone() {
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage(startsWith("No thread-bound SimpAttributes found"));
		SimpAttributesContextHolder.currentAttributes();
	}

}
