

package org.springframework.test.context.web;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.*;

/**
 * Integration test that verifies meta-annotation support for {@link WebAppConfiguration}
 * and {@link ContextConfiguration}.
 *
 * @author Sam Brannen
 * @since 4.0
 * @see WebTestConfiguration
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebTestConfiguration
public class MetaAnnotationConfigWacTests {

	@Autowired
	protected WebApplicationContext wac;

	@Autowired
	protected MockServletContext mockServletContext;

	@Autowired
	protected String foo;


	@Test
	public void fooEnigmaAutowired() {
		assertEquals("enigma", foo);
	}

	@Test
	public void basicWacFeatures() throws Exception {
		assertNotNull("ServletContext should be set in the WAC.", wac.getServletContext());

		assertNotNull("ServletContext should have been autowired from the WAC.", mockServletContext);

		Object rootWac = mockServletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		assertNotNull("Root WAC must be stored in the ServletContext as: "
				+ WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, rootWac);
		assertSame("test WAC and Root WAC in ServletContext must be the same object.", wac, rootWac);
		assertSame("ServletContext instances must be the same object.", mockServletContext, wac.getServletContext());

		assertEquals("Getting real path for ServletContext resource.",
			new File("src/main/webapp/index.jsp").getCanonicalPath(), mockServletContext.getRealPath("index.jsp"));
	}

}
