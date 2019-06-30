

package org.springframework.test.context.hierarchies.web;

import org.junit.Ignore;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.*;

/**
 * @author Sam Brannen
 * @since 3.2.2
 */
@WebAppConfiguration
@ContextHierarchy(@ContextConfiguration)
public class RootWacEarTests extends EarTests {

	@Configuration
	static class RootWacConfig {

		@Bean
		public String root() {
			return "root";
		}
	}


	// -------------------------------------------------------------------------

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private String ear;

	@Autowired
	private String root;


	@Ignore("Superseded by verifyRootWacConfig()")
	@Test
	@Override
	public void verifyEarConfig() {
		/* no-op */
	}

	@Test
	public void verifyRootWacConfig() {
		ApplicationContext parent = wac.getParent();
		assertNotNull(parent);
		assertFalse(parent instanceof WebApplicationContext);
		assertEquals("ear", ear);
		assertEquals("root", root);
	}

}
