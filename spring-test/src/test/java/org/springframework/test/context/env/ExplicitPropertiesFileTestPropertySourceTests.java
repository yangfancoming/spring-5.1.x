

package org.springframework.test.context.env;

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
 * support with an explicitly named properties file.
 *
 * @author Sam Brannen
 * @since 4.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TestPropertySource("explicit.properties")
public class ExplicitPropertiesFileTestPropertySourceTests {

	@Autowired
	protected Environment env;


	@Test
	public void verifyPropertiesAreAvailableInEnvironment() {
		String userHomeKey = "user.home";
		assertEquals(System.getProperty(userHomeKey), env.getProperty(userHomeKey));
		assertEquals("enigma", env.getProperty("explicit"));
	}


	// -------------------------------------------------------------------

	@Configuration
	static class Config {
		/* no user beans required for these tests */
	}

}
