

package org.springframework.test.context.junit4.rules;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.*;

/**
 * Subclass #1 of {@link BaseAppCtxRuleTests}.
 *
 * @author Sam Brannen
 * @since 4.2
 */
@ContextConfiguration
public class Subclass1AppCtxRuleTests extends BaseAppCtxRuleTests {

	@Autowired
	private String bar;


	@Test
	public void bar() {
		assertEquals("bar", bar);
	}


	@Configuration
	static class Config {

		@Bean
		public String bar() {
			return "bar";
		}
	}
}
