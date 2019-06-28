

package org.springframework.context.annotation.spr10546;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Rob Winch
 */
@Configuration
public class ImportedConfig {
	@Bean
	public String myBean() {
		return "myBean";
	}
}
