

package org.springframework.test.context.env;

import org.junit.Test;

import org.springframework.test.context.TestPropertySource;

import static org.junit.Assert.*;

/**
 * Integration tests that verify support for overriding properties from
 * properties files via inlined properties configured with
 * {@link TestPropertySource @TestPropertySource}.
 *
 * @author Sam Brannen
 * @since 4.1
 */
@TestPropertySource(properties = { "explicit = inlined", "extended = inlined1", "extended = inlined2" })
public class MergedPropertiesFilesOverriddenByInlinedPropertiesTestPropertySourceTests extends
		MergedPropertiesFilesTestPropertySourceTests {

	@Test
	@Override
	public void verifyPropertiesAreAvailableInEnvironment() {
		assertEquals("inlined", env.getProperty("explicit"));
	}

	@Test
	@Override
	public void verifyExtendedPropertiesAreAvailableInEnvironment() {
		assertEquals("inlined2", env.getProperty("extended"));
	}

}
