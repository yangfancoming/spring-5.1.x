

package org.springframework.test.context.hierarchies.web;

import javax.servlet.ServletContext;

import org.junit.Ignore;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.*;

/**
 * @author Sam Brannen
 * @since 3.2.2
 */
@ContextHierarchy(@ContextConfiguration)
public class DispatcherWacRootWacEarTests extends RootWacEarTests {

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private String ear;

	@Autowired
	private String root;

	@Autowired
	private String dispatcher;


	@Ignore("Superseded by verifyDispatcherWacConfig()")
	@Test
	@Override
	public void verifyEarConfig() {
		/* no-op */
	}

	@Ignore("Superseded by verifyDispatcherWacConfig()")
	@Test
	@Override
	public void verifyRootWacConfig() {
		/* no-op */
	}

	@Test
	public void verifyDispatcherWacConfig() {
		ApplicationContext parent = wac.getParent();
		assertNotNull(parent);
		assertTrue(parent instanceof WebApplicationContext);

		ApplicationContext grandParent = parent.getParent();
		assertNotNull(grandParent);
		assertFalse(grandParent instanceof WebApplicationContext);

		ServletContext dispatcherServletContext = wac.getServletContext();
		assertNotNull(dispatcherServletContext);
		ServletContext rootServletContext = ((WebApplicationContext) parent).getServletContext();
		assertNotNull(rootServletContext);
		assertSame(dispatcherServletContext, rootServletContext);

		assertSame(parent,
			rootServletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE));
		assertSame(parent,
			dispatcherServletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE));

		assertEquals("ear", ear);
		assertEquals("root", root);
		assertEquals("dispatcher", dispatcher);
	}

}
