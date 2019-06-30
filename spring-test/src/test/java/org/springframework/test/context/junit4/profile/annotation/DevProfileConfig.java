

package org.springframework.test.context.junit4.profile.annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.tests.sample.beans.Employee;

/**
 * @author Sam Brannen
 * @since 3.1
 */
@Profile("dev")
@Configuration
public class DevProfileConfig {

	@Bean
	public Employee employee() {
		Employee employee = new Employee();
		employee.setName("John Smith");
		employee.setAge(42);
		employee.setCompany("Acme Widgets, Inc.");
		return employee;
	}

}
