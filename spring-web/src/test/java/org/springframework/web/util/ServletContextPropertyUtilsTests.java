package org.springframework.web.util;

import org.junit.Test;

import org.springframework.mock.web.test.MockServletContext;

import static org.junit.Assert.*;

/**
 * @since 3.2.2
 */
public class ServletContextPropertyUtilsTests {

	@Test
	public void resolveAsServletContextInitParameter() {
		MockServletContext servletContext = new MockServletContext();
		servletContext.setInitParameter("test.prop", "bar");
		String resolved = ServletContextPropertyUtils.resolvePlaceholders("${test.prop:foo}", servletContext);
		assertEquals("bar", resolved);
	}

	@Test
	public void fallbackToSystemProperties() {
		MockServletContext servletContext = new MockServletContext();
		System.setProperty("test.prop", "bar");
		try {
			String resolved = ServletContextPropertyUtils.resolvePlaceholders("${test.prop:foo}", servletContext);
			assertEquals("bar", resolved);
		}
		finally {
			System.clearProperty("test.prop");
		}
	}
}
