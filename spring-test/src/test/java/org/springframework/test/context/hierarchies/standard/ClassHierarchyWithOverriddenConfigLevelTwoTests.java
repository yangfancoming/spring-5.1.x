

package org.springframework.test.context.hierarchies.standard;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
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
@ContextHierarchy(@ContextConfiguration(name = "child", classes = ClassHierarchyWithOverriddenConfigLevelTwoTests.TestUserConfig.class, inheritLocations = false))
public class ClassHierarchyWithOverriddenConfigLevelTwoTests extends ClassHierarchyWithMergedConfigLevelOneTests {

	@Configuration
	static class TestUserConfig {

		@Autowired
		private ClassHierarchyWithMergedConfigLevelOneTests.AppConfig appConfig;


		@Bean
		public String user() {
			return appConfig.parent() + " + test user";
		}

		@Bean
		public String beanFromTestUserConfig() {
			return "from TestUserConfig";
		}
	}


	@Autowired
	private String beanFromTestUserConfig;


	@Test
	@Override
	public void loadContextHierarchy() {
		assertNotNull("child ApplicationContext", context);
		assertNotNull("parent ApplicationContext", context.getParent());
		assertNull("grandparent ApplicationContext", context.getParent().getParent());
		assertEquals("parent", parent);
		assertEquals("parent + test user", user);
		assertEquals("from TestUserConfig", beanFromTestUserConfig);
		assertNull("Bean from UserConfig should not be present.", beanFromUserConfig);
	}

}
