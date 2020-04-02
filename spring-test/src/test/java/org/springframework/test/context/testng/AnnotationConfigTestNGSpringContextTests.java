

package org.springframework.test.context.testng;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.tests.sample.beans.Employee;
import org.springframework.tests.sample.beans.Pet;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Integration tests that verify support for
 * {@link org.springframework.context.annotation.Configuration @Configuration} classes
 * with TestNG-based tests.
 *
 * Configuration will be loaded from
 * {@link AnnotationConfigTestNGSpringContextTests.Config}.
 *
 * @author Sam Brannen
 * @since 5.1
 */
@ContextConfiguration
public class AnnotationConfigTestNGSpringContextTests extends AbstractTestNGSpringContextTests {

	@Autowired
	Employee employee;

	@Autowired
	Pet pet;

	@Test
	void autowiringFromConfigClass() {
		assertNotNull(employee, "The employee should have been autowired.");
		assertEquals(employee.getName(), "John Smith");

		assertNotNull(pet, "The pet should have been autowired.");
		assertEquals(pet.getName(), "Fido");
	}


	@Configuration
	static class Config {

		@Bean
		Employee employee() {
			return new Employee("John Smith");
		}

		@Bean
		Pet pet() {
			return new Pet("Fido");
		}

	}

}
