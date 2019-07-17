

package org.springframework.test.context.junit4.profile.importresource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.tests.sample.beans.Pet;

/**

 * @since 3.1
 */
@Configuration
@ImportResource("org/springframework/test/context/junit4/profile/importresource/import.xml")
public class DefaultProfileConfig {

	@Bean
	public Pet pet() {
		return new Pet("Fido");
	}

}
