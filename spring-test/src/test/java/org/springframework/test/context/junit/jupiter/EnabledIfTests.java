

package org.springframework.test.context.junit.jupiter;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.SpringJUnitJupiterTestSuite;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Integration tests which verify support for {@link EnabledIf @EnabledIf}
 * in conjunction with the {@link SpringExtension} in a JUnit Jupiter environment.
 *
 * To run these tests in an IDE that does not have built-in support for the JUnit
 * Platform, simply run {@link SpringJUnitJupiterTestSuite} as a JUnit 4 test.
 *
 * @author Tadaya Tsuyukubo
 * @author Sam Brannen
 * @since 5.0
 * @see EnabledIfConditionTests
 * @see EnabledIf
 * @see SpringExtension
 */
class EnabledIfTests {

	@SpringJUnitConfig(Config.class)
	@TestPropertySource(properties = "foo = false")
	@Nested
	class EnabledIfOnMethodTests {

		@Test
		@EnabledIf("false")
		void enabledIfWithStringFalse() {
			fail("This test must be disabled");
		}

		@Test
		@EnabledIf("   false   ")
		void enabledIfWithStringFalseWithSurroundingWhitespace() {
			fail("This test must be disabled");
		}

		@Test
		@EnabledIf("FaLsE")
		void enabledIfWithStringFalseIgnoreCase() {
			fail("This test must be disabled");
		}

		@Test
		@EnabledIf("${__EnigmaPropertyShouldNotExist__:false}")
		void enabledIfWithPropertyPlaceholderForNonexistentPropertyWithDefaultValue() {
			fail("This test must be disabled");
		}

		@Test
		@EnabledIf(expression = "${foo}", loadContext = true)
		void enabledIfWithPropertyPlaceholder() {
			fail("This test must be disabled");
		}

		@Test
		@EnabledIf(expression = "\t${foo}   ", loadContext = true)
		void enabledIfWithPropertyPlaceholderWithSurroundingWhitespace() {
			fail("This test must be disabled");
		}

		@Test
		@EnabledIf("#{T(Boolean).FALSE}")
		void enabledIfWithSpelBoolean() {
			fail("This test must be disabled");
		}

		@Test
		@EnabledIf("   #{T(Boolean).FALSE}   ")
		void enabledIfWithSpelBooleanWithSurroundingWhitespace() {
			fail("This test must be disabled");
		}

		@Test
		@EnabledIf("#{'fal' + 'se'}")
		void enabledIfWithSpelStringConcatenation() {
			fail("This test must be disabled");
		}

		@Test
		@EnabledIf("#{1 + 2 == 4}")
		void enabledIfWithSpelArithmeticComparison() {
			fail("This test must be disabled");
		}

		@Test
		@EnabledOnMac
		void enabledIfWithSpelOsCheckInCustomComposedAnnotation() {
			String os = System.getProperty("os.name").toLowerCase();
			assertTrue(os.contains("mac"), "This test must be enabled on Mac OS");
			assertFalse(os.contains("win"), "This test must be disabled on Windows");
		}

		@Test
		@EnabledIf(expression = "#{@booleanFalseBean}", loadContext = true)
		void enabledIfWithSpelBooleanFalseBean() {
			fail("This test must be disabled");
		}

		@Test
		@EnabledIf(expression = "#{@stringFalseBean}", loadContext = true)
		void enabledIfWithSpelStringFalseBean() {
			fail("This test must be disabled");
		}
	}

	@SpringJUnitConfig(Config.class)
	@Nested
	@EnabledIf("false")
	class EnabledIfOnClassTests {

		@Test
		void foo() {
			fail("This test must be disabled");
		}

		@Test
		@EnabledIf("true")
		void bar() {
			fail("This test must be disabled due to class-level condition");
		}
	}

	@Configuration
	static class Config {

		@Bean
		Boolean booleanFalseBean() {
			return Boolean.FALSE;
		}

		@Bean
		String stringFalseBean() {
			return "false";
		}
	}

}
