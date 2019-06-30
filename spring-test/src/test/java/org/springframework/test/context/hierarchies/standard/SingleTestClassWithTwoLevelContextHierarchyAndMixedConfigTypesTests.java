

package org.springframework.test.context.hierarchies.standard;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @author Sam Brannen
 * @since 3.2.2
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextHierarchy({
	@ContextConfiguration(classes = SingleTestClassWithTwoLevelContextHierarchyAndMixedConfigTypesTests.ParentConfig.class),
	@ContextConfiguration("SingleTestClassWithTwoLevelContextHierarchyAndMixedConfigTypesTests-ChildConfig.xml") })
public class SingleTestClassWithTwoLevelContextHierarchyAndMixedConfigTypesTests {

	@Configuration
	static class ParentConfig {

		@Bean
		public String foo() {
			return "foo";
		}

		@Bean
		public String baz() {
			return "baz-parent";
		}
	}


	@Autowired
	private String foo;

	@Autowired
	private String bar;

	@Autowired
	private String baz;

	@Autowired
	private ApplicationContext context;


	@Test
	public void loadContextHierarchy() {
		assertNotNull("child ApplicationContext", context);
		assertNotNull("parent ApplicationContext", context.getParent());
		assertNull("grandparent ApplicationContext", context.getParent().getParent());
		assertEquals("foo", foo);
		assertEquals("bar", bar);
		assertEquals("baz-child", baz);
	}

}
