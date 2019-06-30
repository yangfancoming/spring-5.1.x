

package org.springframework.test.context.configuration;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextLoader;
import org.springframework.test.context.junit4.PropertiesBasedSpringJUnit4ClassRunnerAppCtxTests;
import org.springframework.tests.sample.beans.Pet;

import static org.junit.Assert.*;

/**
 * Integration tests which verify that the same custom {@link ContextLoader} can
 * be used at all levels within a test class hierarchy when the
 * {@code loader} is <i>inherited</i> (i.e., not explicitly declared) via
 * {@link ContextConfiguration &#064;ContextConfiguration}.
 *
 * @author Sam Brannen
 * @since 3.0
 * @see PropertiesBasedSpringJUnit4ClassRunnerAppCtxTests
 * @see ContextConfigurationWithPropertiesExtendingPropertiesTests
 */
@ContextConfiguration
public class ContextConfigurationWithPropertiesExtendingPropertiesAndInheritedLoaderTests extends
		PropertiesBasedSpringJUnit4ClassRunnerAppCtxTests {

	@Autowired
	private Pet dog;

	@Autowired
	private String testString2;


	@Test
	public void verifyExtendedAnnotationAutowiredFields() {
		assertNotNull("The dog field should have been autowired.", this.dog);
		assertEquals("Fido", this.dog.getName());

		assertNotNull("The testString2 field should have been autowired.", this.testString2);
		assertEquals("Test String #2", this.testString2);
	}

}
