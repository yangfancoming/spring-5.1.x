

package org.springframework.test.context.junit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.junit.Assert.assertEquals;

/**
 * JUnit 4 based integration test which verifies that {@link @ContextConfiguration}
 * is optional.
 *
 * @author Phillip Webb
 * @author Sam Brannen
 * @since 4.3
 */
@RunWith(SpringRunner.class)
public class OptionalContextConfigurationSpringRunnerTests {

	@Autowired
	String foo;


	@Test
	public void contextConfigurationAnnotationIsOptional() {
		assertEquals("foo", foo);
	}


	@Configuration
	static class Config {

		@Bean
		String foo() {
			return "foo";
		}
	}

}
