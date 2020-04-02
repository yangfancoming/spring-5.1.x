

package org.springframework.test.context.junit4.annotation;

import org.junit.Test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.tests.sample.beans.Employee;

import static org.junit.Assert.*;

/**
 * Integration tests that verify support for configuration classes in
 * the Spring TestContext Framework.
 *
 * Configuration will be loaded from {@link DefaultConfigClassesBaseTests.ContextConfiguration}
 * and {@link BeanOverridingDefaultConfigClassesInheritedTests.ContextConfiguration}.
 *
 * @author Sam Brannen
 * @since 3.1
 */
@ContextConfiguration
public class BeanOverridingDefaultConfigClassesInheritedTests extends DefaultConfigClassesBaseTests {

	@Configuration
	static class ContextConfiguration {

		@Bean
		public Employee employee() {
			Employee employee = new Employee();
			employee.setName("Yoda");
			employee.setAge(900);
			employee.setCompany("The Force");
			return employee;
		}
	}


	@Test
	@Override
	public void verifyEmployeeSetFromBaseContextConfig() {
		assertNotNull("The employee should have been autowired.", this.employee);
		assertEquals("The employee bean should have been overridden.", "Yoda", this.employee.getName());
	}

}
