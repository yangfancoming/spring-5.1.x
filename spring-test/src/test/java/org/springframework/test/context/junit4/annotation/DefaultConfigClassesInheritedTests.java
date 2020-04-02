

package org.springframework.test.context.junit4.annotation;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.tests.sample.beans.Pet;

import static org.junit.Assert.*;

/**
 * Integration tests that verify support for configuration classes in
 * the Spring TestContext Framework.
 *
 * Configuration will be loaded from {@link DefaultConfigClassesBaseTests.ContextConfiguration}
 * and {@link DefaultConfigClassesInheritedTests.ContextConfiguration}.
 *
 * @author Sam Brannen
 * @since 3.1
 */
@ContextConfiguration
public class DefaultConfigClassesInheritedTests extends DefaultConfigClassesBaseTests {

	@Configuration
	static class ContextConfiguration {

		@Bean
		public Pet pet() {
			return new Pet("Fido");
		}
	}


	@Autowired
	private Pet pet;


	@Test
	public void verifyPetSetFromExtendedContextConfig() {
		assertNotNull("The pet should have been autowired.", this.pet);
		assertEquals("Fido", this.pet.getName());
	}

}
