

/**
 * Mirrors the structure of beans and environment-specific config files
 * in EnvironmentIntegrationTests-context.xml
 */
package org.springframework.core.env.scan1;

import static org.springframework.core.env.EnvironmentSystemIntegrationTests.Constants.DEV_ENV_NAME;
import static org.springframework.core.env.EnvironmentSystemIntegrationTests.Constants.PROD_ENV_NAME;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Import({DevConfig.class, ProdConfig.class})
class Config {
}

@Profile(DEV_ENV_NAME)
@Configuration
class DevConfig {
	@Bean
	public Object devBean() {
		return new Object();
	}
}

@Profile(PROD_ENV_NAME)
@Configuration
class ProdConfig {
	@Bean
	public Object prodBean() {
		return new Object();
	}
}
