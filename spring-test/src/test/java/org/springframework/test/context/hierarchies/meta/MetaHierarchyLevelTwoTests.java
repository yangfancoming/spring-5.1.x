

package org.springframework.test.context.hierarchies.meta;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.*;

/**
 * @author Sam Brannen
 * @since 4.0.3
 */
@ContextConfiguration
@ActiveProfiles("prod")
public class MetaHierarchyLevelTwoTests extends MetaHierarchyLevelOneTests {

	@Configuration
	@Profile("prod")
	static class Config {

		@Bean
		public String bar() {
			return "Prod Bar";
		}
	}


	@Autowired
	protected ApplicationContext context;

	@Autowired
	private String bar;


	@Test
	public void bar() {
		assertEquals("Prod Bar", bar);
	}

	@Test
	public void contextHierarchy() {
		assertNotNull("child ApplicationContext", context);
		assertNotNull("parent ApplicationContext", context.getParent());
		assertNull("grandparent ApplicationContext", context.getParent().getParent());
	}

}
