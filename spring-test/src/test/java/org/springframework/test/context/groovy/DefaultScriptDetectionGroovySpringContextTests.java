

package org.springframework.test.context.groovy;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.tests.sample.beans.Employee;
import org.springframework.tests.sample.beans.Pet;

import static org.junit.Assert.*;

/**
 * Integration test class that verifies proper detection of a default
 * Groovy script (as opposed to a default XML config file).
 *
 * @author Sam Brannen
 * @since 4.1
 * @see DefaultScriptDetectionGroovySpringContextTestsContext
 */
@RunWith(SpringJUnit4ClassRunner.class)
// Config loaded from DefaultScriptDetectionGroovySpringContextTestsContext.groovy
@ContextConfiguration
public class DefaultScriptDetectionGroovySpringContextTests {

	@Autowired
	private Employee employee;

	@Autowired
	private Pet pet;

	@Autowired
	protected String foo;


	@Test
	public final void verifyAnnotationAutowiredFields() {
		assertNotNull("The employee field should have been autowired.", this.employee);
		assertEquals("Dilbert", this.employee.getName());

		assertNotNull("The pet field should have been autowired.", this.pet);
		assertEquals("Dogbert", this.pet.getName());

		assertEquals("The foo field should have been autowired.", "Foo", this.foo);
	}

}
