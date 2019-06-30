

package org.springframework.test.context.configuration.interfaces;

import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.configuration.interfaces.WebAppConfigurationTestInterface.Config;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author Sam Brannen
 * @since 4.3
 */
@WebAppConfiguration
@ContextConfiguration(classes = Config.class)
interface WebAppConfigurationTestInterface {

	@Configuration
	static class Config {
		/* no user beans required for these tests */
	}

}
