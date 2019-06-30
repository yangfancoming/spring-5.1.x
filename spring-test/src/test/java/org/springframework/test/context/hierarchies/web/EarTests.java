

package org.springframework.test.context.hierarchies.web;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.*;

/**
 * @author Sam Brannen
 * @since 3.2.2
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class EarTests {

	@Configuration
	static class EarConfig {

		@Bean
		public String ear() {
			return "ear";
		}
	}


	// -------------------------------------------------------------------------

	@Autowired
	private ApplicationContext context;

	@Autowired
	private String ear;


	@Test
	public void verifyEarConfig() {
		assertFalse(context instanceof WebApplicationContext);
		assertNull(context.getParent());
		assertEquals("ear", ear);
	}

}
