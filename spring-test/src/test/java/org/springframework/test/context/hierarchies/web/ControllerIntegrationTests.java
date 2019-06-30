

package org.springframework.test.context.hierarchies.web;

import javax.servlet.ServletContext;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.hierarchies.web.ControllerIntegrationTests.AppConfig;
import org.springframework.test.context.hierarchies.web.ControllerIntegrationTests.WebConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.*;

/**
 * @author Sam Brannen
 * @since 3.2.2
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextHierarchy({
	//
	@ContextConfiguration(name = "root", classes = AppConfig.class),
	@ContextConfiguration(name = "dispatcher", classes = WebConfig.class) //
})
public class ControllerIntegrationTests {

	@Configuration
	static class AppConfig {

		@Bean
		public String foo() {
			return "foo";
		}
	}

	@Configuration
	static class WebConfig {

		@Bean
		public String bar() {
			return "bar";
		}
	}


	// -------------------------------------------------------------------------

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private String foo;

	@Autowired
	private String bar;


	@Test
	public void verifyRootWacSupport() {
		assertEquals("foo", foo);
		assertEquals("bar", bar);

		ApplicationContext parent = wac.getParent();
		assertNotNull(parent);
		assertTrue(parent instanceof WebApplicationContext);
		WebApplicationContext root = (WebApplicationContext) parent;
		assertFalse(root.getBeansOfType(String.class).containsKey("bar"));

		ServletContext childServletContext = wac.getServletContext();
		assertNotNull(childServletContext);
		ServletContext rootServletContext = root.getServletContext();
		assertNotNull(rootServletContext);
		assertSame(childServletContext, rootServletContext);

		assertSame(root, rootServletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE));
		assertSame(root, childServletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE));
	}

}
