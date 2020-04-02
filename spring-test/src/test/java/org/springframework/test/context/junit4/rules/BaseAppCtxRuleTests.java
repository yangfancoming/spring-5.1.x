

package org.springframework.test.context.junit4.rules;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.*;

/**
 * Base class for integration tests involving Spring {@code ApplicationContexts}
 * in conjunction with {@link SpringClassRule} and {@link SpringMethodRule}.
 *
 * The goal of this class and its subclasses is to ensure that Rule-based
 * configuration can be inherited without requiring {@link SpringClassRule}
 * or {@link SpringMethodRule} to be redeclared on subclasses.
 *
 * @author Sam Brannen
 * @since 4.2
 * @see Subclass1AppCtxRuleTests
 * @see Subclass2AppCtxRuleTests
 */
@ContextConfiguration
public class BaseAppCtxRuleTests {

	@ClassRule
	public static final SpringClassRule springClassRule = new SpringClassRule();

	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();

	@Autowired
	private String foo;


	@Test
	public void foo() {
		assertEquals("foo", foo);
	}


	@Configuration
	static class Config {

		@Bean
		public String foo() {
			return "foo";
		}
	}
}
