

package org.springframework.test.context.env;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Integration tests for {@link TestPropertySource @TestPropertySource} support with
 * inlined properties that overrides properties files.
 *
 * @author Sam Brannen
 * @since 4.3
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TestPropertySource(locations = "explicit.properties", properties = "explicit = inlined")
public class InlinedPropertiesOverridePropertiesFilesTestPropertySourceTests {

	@Autowired
	Environment env;

	@Value("${explicit}")
	String explicit;


	@Test
	public void inlinedPropertyOverridesValueFromPropertiesFile() {
		assertEquals("inlined", env.getProperty("explicit"));
		assertEquals("inlined", this.explicit);
	}


	@Configuration
	static class Config {
	}

}
