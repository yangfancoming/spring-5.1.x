

package org.springframework.test.context.junit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextLoader;
import org.springframework.test.context.support.DefaultTestContextBootstrapper;
import org.springframework.test.context.support.GenericPropertiesContextLoader;
import org.springframework.tests.sample.beans.Pet;

import static org.junit.Assert.*;

/**
 * Integration tests which verify that a subclass of {@link DefaultTestContextBootstrapper}
 * can specify a custom <em>default ContextLoader class</em> that overrides the standard
 * default class name.
 *
 * @author Sam Brannen
 * @since 3.0
 */
@RunWith(SpringRunner.class)
@BootstrapWith(CustomDefaultContextLoaderClassSpringRunnerTests.PropertiesBasedTestContextBootstrapper.class)
@ContextConfiguration("PropertiesBasedSpringJUnit4ClassRunnerAppCtxTests-context.properties")
public class CustomDefaultContextLoaderClassSpringRunnerTests {

	@Autowired
	private Pet cat;

	@Autowired
	private String testString;


	@Test
	public void verifyAnnotationAutowiredFields() {
		assertNotNull("The cat field should have been autowired.", this.cat);
		assertEquals("Garfield", this.cat.getName());

		assertNotNull("The testString field should have been autowired.", this.testString);
		assertEquals("Test String", this.testString);
	}


	public static class PropertiesBasedTestContextBootstrapper extends DefaultTestContextBootstrapper {

		@Override
		protected Class<? extends ContextLoader> getDefaultContextLoaderClass(Class<?> testClass) {
			return GenericPropertiesContextLoader.class;
		}
	}

}
