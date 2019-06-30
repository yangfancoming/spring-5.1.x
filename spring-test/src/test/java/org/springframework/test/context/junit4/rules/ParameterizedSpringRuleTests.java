

package org.springframework.test.context.junit4.rules;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.tests.sample.beans.Employee;
import org.springframework.tests.sample.beans.Pet;

import static org.junit.Assert.*;

/**
 * Integration test which demonstrates how to use JUnit's {@link Parameterized}
 * runner in conjunction with {@link SpringClassRule} and {@link SpringMethodRule}
 * to provide dependency injection to a <em>parameterized test instance</em>.
 *
 * @author Sam Brannen
 * @since 4.2
 * @see org.springframework.test.context.junit4.ParameterizedDependencyInjectionTests
 */
@RunWith(Parameterized.class)
@ContextConfiguration("/org/springframework/test/context/junit4/ParameterizedDependencyInjectionTests-context.xml")
public class ParameterizedSpringRuleTests {

	private static final AtomicInteger invocationCount = new AtomicInteger();

	@ClassRule
	public static final SpringClassRule springClassRule = new SpringClassRule();

	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private Pet pet;

	@Parameter(0)
	public String employeeBeanName;

	@Parameter(1)
	public String employeeName;


	@Parameters(name = "bean [{0}], employee [{1}]")
	public static String[][] employeeData() {
		return new String[][] { { "employee1", "John Smith" }, { "employee2", "Jane Smith" } };
	}

	@BeforeClass
	public static void BeforeClass() {
		invocationCount.set(0);
	}

	@Test
	public final void verifyPetAndEmployee() {
		invocationCount.incrementAndGet();

		// Verifying dependency injection:
		assertNotNull("The pet field should have been autowired.", this.pet);

		// Verifying 'parameterized' support:
		Employee employee = this.applicationContext.getBean(this.employeeBeanName, Employee.class);
		assertEquals("Name of the employee configured as bean [" + this.employeeBeanName + "].", this.employeeName,
			employee.getName());
	}

	@AfterClass
	public static void verifyNumParameterizedRuns() {
		assertEquals("Number of times the parameterized test method was executed.", employeeData().length,
			invocationCount.get());
	}

}
