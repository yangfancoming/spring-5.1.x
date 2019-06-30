

package org.springframework.test.context.env;

import org.junit.Test;

import org.springframework.test.context.TestPropertySource;

/**
 * Integration tests that verify detection of default properties files
 * when {@link TestPropertySource @TestPropertySource} is <em>empty</em>
 * at multiple levels within a class hierarchy.
 *
 * @author Sam Brannen
 * @since 4.1
 */
@TestPropertySource
public class ExtendedDefaultPropertiesFileDetectionTestPropertySourceTests extends
		DefaultPropertiesFileDetectionTestPropertySourceTests {

	@Test
	public void verifyPropertiesAreAvailableInEnvironment() {
		super.verifyPropertiesAreAvailableInEnvironment();
		// from ExtendedDefaultPropertiesFileDetectionTestPropertySourceTests.properties
		assertEnvironmentValue("enigma", "auto detected");
	}

}
