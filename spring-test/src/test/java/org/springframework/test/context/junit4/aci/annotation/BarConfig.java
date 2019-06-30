

package org.springframework.test.context.junit4.aci.annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Sam Brannen
 * @since 4.3
 */
@Configuration
class BarConfig {

	@Bean
	String bar() {
		return "bar";
	}

}
