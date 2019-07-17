

package org.springframework.jndi;

import javax.naming.Context;
import javax.naming.NamingException;

import org.junit.Test;

import org.springframework.tests.mock.jndi.SimpleNamingContext;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link JndiPropertySource}.


 * @since 3.1
 */
public class JndiPropertySourceTests {

	@Test
	public void nonExistentProperty() {
		JndiPropertySource ps = new JndiPropertySource("jndiProperties");
		assertThat(ps.getProperty("bogus"), nullValue());
	}

	@Test
	public void nameBoundWithoutPrefix() {
		final SimpleNamingContext context = new SimpleNamingContext();
		context.bind("p1", "v1");

		JndiTemplate jndiTemplate = new JndiTemplate() {
			@Override
			protected Context createInitialContext() throws NamingException {
				return context;
			}
		};
		JndiLocatorDelegate jndiLocator = new JndiLocatorDelegate();
		jndiLocator.setResourceRef(true);
		jndiLocator.setJndiTemplate(jndiTemplate);

		JndiPropertySource ps = new JndiPropertySource("jndiProperties", jndiLocator);
		assertThat(ps.getProperty("p1"), equalTo("v1"));
	}

	@Test
	public void nameBoundWithPrefix() {
		final SimpleNamingContext context = new SimpleNamingContext();
		context.bind("java:comp/env/p1", "v1");

		JndiTemplate jndiTemplate = new JndiTemplate() {
			@Override
			protected Context createInitialContext() throws NamingException {
				return context;
			}
		};
		JndiLocatorDelegate jndiLocator = new JndiLocatorDelegate();
		jndiLocator.setResourceRef(true);
		jndiLocator.setJndiTemplate(jndiTemplate);

		JndiPropertySource ps = new JndiPropertySource("jndiProperties", jndiLocator);
		assertThat(ps.getProperty("p1"), equalTo("v1"));
	}

	@Test
	public void propertyWithDefaultClauseInResourceRefMode() {
		JndiLocatorDelegate jndiLocator = new JndiLocatorDelegate() {
			@Override
			public Object lookup(String jndiName) throws NamingException {
				throw new IllegalStateException("Should not get called");
			}
		};
		jndiLocator.setResourceRef(true);

		JndiPropertySource ps = new JndiPropertySource("jndiProperties", jndiLocator);
		assertThat(ps.getProperty("propertyKey:defaultValue"), nullValue());
	}

	@Test
	public void propertyWithColonInNonResourceRefMode() {
		JndiLocatorDelegate jndiLocator = new JndiLocatorDelegate() {
			@Override
			public Object lookup(String jndiName) throws NamingException {
				assertEquals("my:key", jndiName);
				return "my:value";
			}
		};
		jndiLocator.setResourceRef(false);

		JndiPropertySource ps = new JndiPropertySource("jndiProperties", jndiLocator);
		assertThat(ps.getProperty("my:key"), equalTo("my:value"));
	}

}
