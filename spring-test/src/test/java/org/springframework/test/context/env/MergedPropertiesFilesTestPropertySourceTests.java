

package org.springframework.test.context.env;

import org.junit.Test;

import org.springframework.test.context.TestPropertySource;

import static org.junit.Assert.*;

/**
 * Integration tests that verify support for contributing additional properties
 * files to the Spring {@code Environment} via {@link TestPropertySource @TestPropertySource}.
 *
 * @author Sam Brannen
 * @since 4.1
 */
@TestPropertySource("extended.properties")
public class MergedPropertiesFilesTestPropertySourceTests extends
		ExplicitPropertiesFileTestPropertySourceTests {

	@Test
	public void verifyExtendedPropertiesAreAvailableInEnvironment() {
		assertEquals(42, env.getProperty("extended", Integer.class).intValue());
	}

}
