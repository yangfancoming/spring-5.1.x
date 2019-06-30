

package org.springframework.test.context.web;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfigurationBootstrapWithTests.CustomWebTestContextBootstrapper;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * JUnit-based integration tests that verify support for loading a
 * {@link WebApplicationContext} with a custom {@link WebTestContextBootstrapper}.
 *
 * @author Sam Brannen
 * @author Phillip Webb
 * @since 4.3
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@WebAppConfiguration
@BootstrapWith(CustomWebTestContextBootstrapper.class)
public class WebAppConfigurationBootstrapWithTests {

	@Autowired
	WebApplicationContext wac;


	@Test
	public void webApplicationContextIsLoaded() {
		// from: src/test/webapp/resources/Spring.js
		Resource resource = wac.getResource("/resources/Spring.js");
		assertNotNull(resource);
		assertTrue(resource.exists());
	}


	@Configuration
	static class Config {
	}

	/**
	 * Custom {@link WebTestContextBootstrapper} that requires {@code @WebAppConfiguration}
	 * but hard codes the resource base path.
	 */
	static class CustomWebTestContextBootstrapper extends WebTestContextBootstrapper {

		@Override
		protected MergedContextConfiguration processMergedContextConfiguration(MergedContextConfiguration mergedConfig) {
			return new WebMergedContextConfiguration(mergedConfig, "src/test/webapp");
		}
	}

}
