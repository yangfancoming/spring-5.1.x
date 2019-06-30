

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
@ContextHierarchy(@ContextConfiguration(name = "child", classes = ClassHierarchyWithMergedConfigLevelTwoTests.OrderConfig.class))
public class ClassHierarchyWithMergedConfigLevelTwoTests extends ClassHierarchyWithMergedConfigLevelOneTests {

	@Configuration
	static class OrderConfig {

		@Autowired
		private ClassHierarchyWithMergedConfigLevelOneTests.UserConfig userConfig;

		@Bean
		public String order() {
			return userConfig.user() + " + order";
		}
	}


	@Autowired
	private String order;


	@Test
	@Override
	public void loadContextHierarchy() {
		super.loadContextHierarchy();
		assertEquals("parent + user + order", order);
	}

}
