

package org.springframework.test.context.env;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.context.support.TestPropertySourceUtils.*;

/**
 * Integration tests for {@link TestPropertySource @TestPropertySource} support with
 * inlined properties.
 *
 * @author Sam Brannen
 * @since 4.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TestPropertySource(properties = { "", "foo = bar", "baz quux", "enigma: 42", "x.y.z = a=b=c",
	"server.url = https://example.com", "key.value.1: key=value", "key.value.2 key=value", "key.value.3 key:value" })
public class InlinedPropertiesTestPropertySourceTests {

	@Autowired
	private ConfigurableEnvironment env;


	private String property(String key) {
		return env.getProperty(key);
	}

	@Test
	public void propertiesAreAvailableInEnvironment() {
		// Simple key/value pairs
		assertThat(property("foo"), is("bar"));
		assertThat(property("baz"), is("quux"));
		assertThat(property("enigma"), is("42"));

		// Values containing key/value delimiters (":", "=", " ")
		assertThat(property("x.y.z"), is("a=b=c"));
		assertThat(property("server.url"), is("https://example.com"));
		assertThat(property("key.value.1"), is("key=value"));
		assertThat(property("key.value.2"), is("key=value"));
		assertThat(property("key.value.3"), is("key:value"));
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void propertyNameOrderingIsPreservedInEnvironment() {
		final String[] expectedPropertyNames = new String[] { "foo", "baz", "enigma", "x.y.z", "server.url",
			"key.value.1", "key.value.2", "key.value.3" };
		EnumerablePropertySource eps = (EnumerablePropertySource) env.getPropertySources().get(
			INLINED_PROPERTIES_PROPERTY_SOURCE_NAME);
		assertArrayEquals(expectedPropertyNames, eps.getPropertyNames());
	}


	// -------------------------------------------------------------------

	@Configuration
	static class Config {
		/* no user beans required for these tests */
	}

}
