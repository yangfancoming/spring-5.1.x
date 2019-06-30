

package org.springframework.test.context.junit4.rules;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.junit.Assert.*;

/**
 * Integration tests for an issue raised in https://jira.spring.io/browse/SPR-15927.
 *
 * @author Sam Brannen
 * @since 5.0
 */
public class AutowiredRuleTests {

	@ClassRule
	public static final SpringClassRule springClassRule = new SpringClassRule();

	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();

	@Autowired
	@Rule
	public AutowiredTestRule autowiredTestRule;

	@Test
	public void test() {
		assertNotNull("TestRule should have been @Autowired", autowiredTestRule);

		// Rationale for the following assertion:
		//
		// The field value for the custom rule is null when JUnit sees it. JUnit then
		// ignores the null value, and at a later point in time Spring injects the rule
		// from the ApplicationContext and overrides the null field value. But that's too
		// late: JUnit never sees the rule supplied by Spring via dependency injection.
		assertFalse("@Autowired TestRule should NOT have been applied", autowiredTestRule.applied);
	}

	@Configuration
	static class Config {

		@Bean
		AutowiredTestRule autowiredTestRule() {
			return new AutowiredTestRule();
		}
	}

	static class AutowiredTestRule implements TestRule {

		private boolean applied = false;

		@Override
		public Statement apply(Statement base, Description description) {
			this.applied = true;
			return base;
		}
	}

}
