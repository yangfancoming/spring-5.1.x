

package org.springframework.test.context.configuration.interfaces;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author Sam Brannen
 * @since 4.3
 */
@RunWith(SpringRunner.class)
public class TestPropertySourceInterfaceTests implements TestPropertySourceTestInterface {

	@Autowired
	Environment env;


	@Test
	public void propertiesAreAvailableInEnvironment() {
		assertThat(property("foo"), is("bar"));
		assertThat(property("enigma"), is("42"));
	}

	private String property(String key) {
		return env.getProperty(key);
	}


	@Configuration
	static class Config {
		/* no user beans required for these tests */
	}

}
