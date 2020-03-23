

package org.springframework.web.reactive.function.server;

import org.junit.Test;

import static org.junit.Assert.*;


public class HandlerStrategiesTests {

	@Test
	public void empty() {
		HandlerStrategies strategies = HandlerStrategies.empty().build();
		assertTrue(strategies.messageReaders().isEmpty());
		assertTrue(strategies.messageWriters().isEmpty());
		assertTrue(strategies.viewResolvers().isEmpty());
	}

	@Test
	public void withDefaults() {
		HandlerStrategies strategies = HandlerStrategies.withDefaults();
		assertFalse(strategies.messageReaders().isEmpty());
		assertFalse(strategies.messageWriters().isEmpty());
		assertTrue(strategies.viewResolvers().isEmpty());
	}

}

