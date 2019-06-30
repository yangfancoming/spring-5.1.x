

package org.springframework.test.context.junit4.profile.annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.tests.sample.beans.Pet;

/**
 * @author Sam Brannen
 * @since 3.1
 */
@Configuration
public class DefaultProfileConfig {

	@Bean
	public Pet pet() {
		return new Pet("Fido");
	}

}
