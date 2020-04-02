

package org.springframework.test.context.junit4.annotation;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.tests.sample.beans.Pet;

import static org.junit.Assert.*;

/**
 * Integration tests that verify support for configuration classes in
 * the Spring TestContext Framework.
 *
 * Configuration will be loaded from {@link DefaultConfigClassesInheritedTests.ContextConfiguration}
 * and {@link DefaultConfigClassesBaseTests.ContextConfiguration}.
 *
 * @author Sam Brannen
 * @since 3.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = DefaultConfigClassesInheritedTests.ContextConfiguration.class)
public class ExplicitConfigClassesInheritedTests extends ExplicitConfigClassesBaseTests {

	@Autowired
	private Pet pet;


	@Test
	public void verifyPetSetFromExtendedContextConfig() {
		assertNotNull("The pet should have been autowired.", this.pet);
		assertEquals("Fido", this.pet.getName());
	}

}
