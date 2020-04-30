

package org.springframework.messaging.simp;

import java.util.concurrent.ConcurrentHashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.ObjectFactory;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

/**
 * Unit tests for {@link org.springframework.messaging.simp.SimpSessionScope}.
 *
 *
 * @since 4.1
 */
public class SimpSessionScopeTests {

	private SimpSessionScope scope;

	@SuppressWarnings("rawtypes")
	private ObjectFactory objectFactory;

	private SimpAttributes simpAttributes;


	@Before
	public void setUp() {
		this.scope = new SimpSessionScope();
		this.objectFactory = Mockito.mock(ObjectFactory.class);
		this.simpAttributes = new SimpAttributes("session1", new ConcurrentHashMap<>());
		SimpAttributesContextHolder.setAttributes(this.simpAttributes);
	}

	@After
	public void tearDown() {
		SimpAttributesContextHolder.resetAttributes();
	}

	@Test
	public void get() {
		this.simpAttributes.setAttribute("name", "value");
		Object actual = this.scope.get("name", this.objectFactory);

		assertThat(actual, is("value"));
	}

	@Test
	public void getWithObjectFactory() {
		given(this.objectFactory.getObject()).willReturn("value");
		Object actual = this.scope.get("name", this.objectFactory);

		assertThat(actual, is("value"));
		assertThat(this.simpAttributes.getAttribute("name"), is("value"));
	}

	@Test
	public void remove() {
		this.simpAttributes.setAttribute("name", "value");

		Object removed = this.scope.remove("name");
		assertThat(removed, is("value"));
		assertThat(this.simpAttributes.getAttribute("name"), nullValue());

		removed = this.scope.remove("name");
		assertThat(removed, nullValue());
	}

	@Test
	public void registerDestructionCallback() {
		Runnable runnable = Mockito.mock(Runnable.class);
		this.scope.registerDestructionCallback("name", runnable);

		this.simpAttributes.sessionCompleted();
		verify(runnable, times(1)).run();
	}

	@Test
	public void getSessionId() {
		assertThat(this.scope.getConversationId(), is("session1"));
	}


}
