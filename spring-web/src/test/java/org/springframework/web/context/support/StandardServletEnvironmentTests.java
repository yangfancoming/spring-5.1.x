

package org.springframework.web.context.support;

import org.junit.Test;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.tests.mock.jndi.SimpleNamingContextBuilder;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link StandardServletEnvironment}.
 *
 * @author Chris Beams
 * @since 3.1
 */
public class StandardServletEnvironmentTests {

	@Test
	public void propertySourceOrder() throws Exception {
		SimpleNamingContextBuilder.emptyActivatedContextBuilder();

		ConfigurableEnvironment env = new StandardServletEnvironment();
		MutablePropertySources sources = env.getPropertySources();

		assertThat(sources.precedenceOf(PropertySource.named(
				StandardServletEnvironment.SERVLET_CONFIG_PROPERTY_SOURCE_NAME)), equalTo(0));
		assertThat(sources.precedenceOf(PropertySource.named(
				StandardServletEnvironment.SERVLET_CONTEXT_PROPERTY_SOURCE_NAME)), equalTo(1));
		assertThat(sources.precedenceOf(PropertySource.named(
				StandardServletEnvironment.JNDI_PROPERTY_SOURCE_NAME)), equalTo(2));
		assertThat(sources.precedenceOf(PropertySource.named(
				StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME)), equalTo(3));
		assertThat(sources.precedenceOf(PropertySource.named(
				StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME)), equalTo(4));
		assertThat(sources.size(), is(5));
	}

}
