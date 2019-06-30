

package org.springframework.test.context.hierarchies.standard;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @author Sam Brannen
 * @since 3.2.2
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TestHierarchyLevelOneWithBareContextConfigurationInSuperclassTests {

	@Configuration
	static class Config {

		@Bean
		public String foo() {
			return "foo-level-1";
		}

		@Bean
		public String bar() {
			return "bar";
		}
	}


	@Autowired
	private String foo;

	@Autowired
	private String bar;

	@Autowired
	private ApplicationContext context;


	@Test
	public void loadContextHierarchy() {
		assertNotNull("child ApplicationContext", context);
		assertNull("parent ApplicationContext", context.getParent());
		assertEquals("foo-level-1", foo);
		assertEquals("bar", bar);
	}

}
