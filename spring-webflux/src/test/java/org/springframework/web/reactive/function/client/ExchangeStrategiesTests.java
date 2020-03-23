

package org.springframework.web.reactive.function.client;

import org.junit.Test;

import static org.junit.Assert.*;


public class ExchangeStrategiesTests {

	@Test
	public void empty() {
		ExchangeStrategies strategies = ExchangeStrategies.empty().build();
		assertTrue(strategies.messageReaders().isEmpty());
		assertTrue(strategies.messageWriters().isEmpty());
	}

	@Test
	public void withDefaults() {
		ExchangeStrategies strategies = ExchangeStrategies.withDefaults();
		assertFalse(strategies.messageReaders().isEmpty());
		assertFalse(strategies.messageWriters().isEmpty());
	}

}
