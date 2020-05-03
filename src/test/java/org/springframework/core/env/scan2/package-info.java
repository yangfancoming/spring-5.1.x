

/**
 * Mirrors the structure of beans and environment-specific config files
 * in EnvironmentIntegrationTests-context.xml
 */
package org.springframework.core.env.scan2;

import static org.springframework.core.env.EnvironmentSystemIntegrationTests.Constants.DEV_BEAN_NAME;
import static org.springframework.core.env.EnvironmentSystemIntegrationTests.Constants.DEV_ENV_NAME;
import static org.springframework.core.env.EnvironmentSystemIntegrationTests.Constants.PROD_BEAN_NAME;
import static org.springframework.core.env.EnvironmentSystemIntegrationTests.Constants.PROD_ENV_NAME;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile(DEV_ENV_NAME)
@Component(DEV_BEAN_NAME)
class DevBean { }

@Profile(PROD_ENV_NAME)
@Component(PROD_BEAN_NAME)
class ProdBean { }
