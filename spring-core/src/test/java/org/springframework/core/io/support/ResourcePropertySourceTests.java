

package org.springframework.core.io.support;

import java.io.IOException;

import org.junit.Test;

import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link ResourcePropertySource}.
 *
 * @author Chris Beams
 * @author Sam Brannen
 * @since 3.1
 */
public class ResourcePropertySourceTests {

	private static final String PROPERTIES_PATH = "org/springframework/core/io/example.properties";
	private static final String PROPERTIES_LOCATION = "classpath:" + PROPERTIES_PATH;
	private static final String PROPERTIES_RESOURCE_DESCRIPTION = "class path resource [" + PROPERTIES_PATH + "]";

	private static final String XML_PROPERTIES_PATH = "org/springframework/core/io/example.xml";
	private static final String XML_PROPERTIES_LOCATION = "classpath:" + XML_PROPERTIES_PATH;
	private static final String XML_PROPERTIES_RESOURCE_DESCRIPTION = "class path resource [" + XML_PROPERTIES_PATH + "]";

	@Test
	public void withLocationAndGeneratedName() throws IOException {
		PropertySource<?> ps = new ResourcePropertySource(PROPERTIES_LOCATION);
		assertEquals("bar", ps.getProperty("foo"));
		assertThat(ps.getName(), is(PROPERTIES_RESOURCE_DESCRIPTION));
	}

	@Test
	public void xmlWithLocationAndGeneratedName() throws IOException {
		PropertySource<?> ps = new ResourcePropertySource(XML_PROPERTIES_LOCATION);
		assertEquals("bar", ps.getProperty("foo"));
		assertThat(ps.getName(), is(XML_PROPERTIES_RESOURCE_DESCRIPTION));
	}

	@Test
	public void withLocationAndExplicitName() throws IOException {
		PropertySource<?> ps = new ResourcePropertySource("ps1", PROPERTIES_LOCATION);
		assertEquals("bar", ps.getProperty("foo"));
		assertThat(ps.getName(), is("ps1"));
	}

	@Test
	public void withLocationAndExplicitNameAndExplicitClassLoader() throws IOException {
		PropertySource<?> ps = new ResourcePropertySource("ps1", PROPERTIES_LOCATION, getClass().getClassLoader());
		assertEquals("bar", ps.getProperty("foo"));
		assertThat(ps.getName(), is("ps1"));
	}

	@Test
	public void withLocationAndGeneratedNameAndExplicitClassLoader() throws IOException {
		PropertySource<?> ps = new ResourcePropertySource(PROPERTIES_LOCATION, getClass().getClassLoader());
		assertEquals("bar", ps.getProperty("foo"));
		assertThat(ps.getName(), is(PROPERTIES_RESOURCE_DESCRIPTION));
	}

	@Test
	public void withResourceAndGeneratedName() throws IOException {
		PropertySource<?> ps = new ResourcePropertySource(new ClassPathResource(PROPERTIES_PATH));
		assertEquals("bar", ps.getProperty("foo"));
		assertThat(ps.getName(), is(PROPERTIES_RESOURCE_DESCRIPTION));
	}

	@Test
	public void withResourceAndExplicitName() throws IOException {
		PropertySource<?> ps = new ResourcePropertySource("ps1", new ClassPathResource(PROPERTIES_PATH));
		assertEquals("bar", ps.getProperty("foo"));
		assertThat(ps.getName(), is("ps1"));
	}

	@Test
	public void withResourceHavingNoDescription() throws IOException {
		PropertySource<?> ps = new ResourcePropertySource(new ByteArrayResource("foo=bar".getBytes(), ""));
		assertEquals("bar", ps.getProperty("foo"));
		assertEquals("Byte array resource []", ps.getName());
	}

}
