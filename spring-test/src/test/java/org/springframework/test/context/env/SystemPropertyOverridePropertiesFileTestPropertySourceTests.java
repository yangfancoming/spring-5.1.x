

package org.springframework.test.context.env;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Integration tests for {@link TestPropertySource @TestPropertySource}
 * support with an explicitly named properties file that overrides a
 * system property.
 *
 * @author Sam Brannen
 * @since 4.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TestPropertySource("SystemPropertyOverridePropertiesFileTestPropertySourceTests.properties")
public class SystemPropertyOverridePropertiesFileTestPropertySourceTests {

	private static final String KEY = SystemPropertyOverridePropertiesFileTestPropertySourceTests.class.getSimpleName() + ".riddle";

	@Autowired
	protected Environment env;


	@BeforeClass
	public static void setSystemProperty() {
		System.setProperty(KEY, "override me!");
	}

	@AfterClass
	public static void removeSystemProperty() {
		System.setProperty(KEY, "");
	}

	@Test
	public void verifyPropertiesAreAvailableInEnvironment() {
		assertEquals("enigma", env.getProperty(KEY));
	}


	// -------------------------------------------------------------------

	@Configuration
	static class Config {
		/* no user beans required for these tests */
	}

}
