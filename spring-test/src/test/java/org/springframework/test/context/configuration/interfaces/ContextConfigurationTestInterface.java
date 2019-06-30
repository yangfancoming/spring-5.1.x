

package org.springframework.test.context.configuration.interfaces;

import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.configuration.interfaces.ContextConfigurationTestInterface.Config;
import org.springframework.tests.sample.beans.Employee;

/**
 * @author Sam Brannen
 * @since 4.3
 */
@ContextConfiguration(classes = Config.class)
interface ContextConfigurationTestInterface {

	static class Config {

		@Bean
		Employee employee() {
			return new Employee("Dilbert");
		}
	}

}
