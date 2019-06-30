

package org.springframework.test.context.configuration.interfaces;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.tests.sample.beans.Employee;

import static org.junit.Assert.*;

/**
 * @author Sam Brannen
 * @since 4.3
 */
@RunWith(SpringRunner.class)
public class ActiveProfilesInterfaceTests implements ActiveProfilesTestInterface {

	@Autowired
	Employee employee;


	@Test
	public void profileFromTestInterface() {
		assertNotNull(employee);
		assertEquals("dev", employee.getName());
	}


	@Configuration
	static class Config {

		@Bean
		@Profile("dev")
		Employee employee1() {
			return new Employee("dev");
		}

		@Bean
		@Profile("prod")
		Employee employee2() {
			return new Employee("prod");
		}
	}

}
