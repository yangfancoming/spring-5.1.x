

package org.springframework.context.support;

import org.junit.Test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Tests covering the integration of the {@link Environment} into
 * {@link ApplicationContext} hierarchies.

 * @see org.springframework.core.env.EnvironmentSystemIntegrationTests
 */
public class EnvironmentIntegrationTests {

	@Test
	public void repro() {
		ConfigurableApplicationContext parent = new GenericApplicationContext();
		parent.refresh();

		AnnotationConfigApplicationContext child = new AnnotationConfigApplicationContext();
		child.setParent(parent);
		child.refresh();

		ConfigurableEnvironment env = child.getBean(ConfigurableEnvironment.class);
		assertThat("unknown env", env, anyOf(
				sameInstance(parent.getEnvironment()),
				sameInstance(child.getEnvironment())));
		assertThat("expected child ctx env", env, sameInstance(child.getEnvironment()));

		child.close();
		parent.close();
	}

}
