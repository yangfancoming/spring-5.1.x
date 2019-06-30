

package org.springframework.test.context.hierarchies.standard;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
//
	@ContextConfiguration(name = "parent", classes = ClassHierarchyWithMergedConfigLevelOneTests.AppConfig.class),//
	@ContextConfiguration(name = "child", classes = ClassHierarchyWithMergedConfigLevelOneTests.UserConfig.class) //
})
public class ClassHierarchyWithMergedConfigLevelOneTests {

	@Configuration
	static class AppConfig {

		@Bean
		public String parent() {
			return "parent";
		}
	}

	@Configuration
	static class UserConfig {

		@Autowired
		private AppConfig appConfig;


		@Bean
		public String user() {
			return appConfig.parent() + " + user";
		}

		@Bean
		public String beanFromUserConfig() {
			return "from UserConfig";
		}
	}


	@Autowired
	protected String parent;

	@Autowired
	protected String user;

	@Autowired(required = false)
	@Qualifier("beanFromUserConfig")
	protected String beanFromUserConfig;

	@Autowired
	protected ApplicationContext context;


	@Test
	public void loadContextHierarchy() {
		assertNotNull("child ApplicationContext", context);
		assertNotNull("parent ApplicationContext", context.getParent());
		assertNull("grandparent ApplicationContext", context.getParent().getParent());
		assertEquals("parent", parent);
		assertEquals("parent + user", user);
		assertEquals("from UserConfig", beanFromUserConfig);
	}

}
