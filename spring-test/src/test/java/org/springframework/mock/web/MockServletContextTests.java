

package org.springframework.mock.web;

import java.util.Map;
import java.util.Set;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRegistration;

import org.junit.Test;

import org.springframework.http.MediaType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**


 * @author Sam Brannen
 * @since 19.02.2006
 */
public class MockServletContextTests {

	private final MockServletContext sc = new MockServletContext("org/springframework/mock");


	@Test
	public void listFiles() {
		Set<String> paths = sc.getResourcePaths("/web");
		assertNotNull(paths);
		assertTrue(paths.contains("/web/MockServletContextTests.class"));
	}

	@Test
	public void listSubdirectories() {
		Set<String> paths = sc.getResourcePaths("/");
		assertNotNull(paths);
		assertTrue(paths.contains("/web/"));
	}

	@Test
	public void listNonDirectory() {
		Set<String> paths = sc.getResourcePaths("/web/MockServletContextTests.class");
		assertNull(paths);
	}

	@Test
	public void listInvalidPath() {
		Set<String> paths = sc.getResourcePaths("/web/invalid");
		assertNull(paths);
	}

	@Test
	public void registerContextAndGetContext() {
		MockServletContext sc2 = new MockServletContext();
		sc.setContextPath("/");
		sc.registerContext("/second", sc2);
		assertSame(sc, sc.getContext("/"));
		assertSame(sc2, sc.getContext("/second"));
	}

	@Test
	public void getMimeType() {
		assertEquals("text/html", sc.getMimeType("test.html"));
		assertEquals("image/gif", sc.getMimeType("test.gif"));
		assertNull(sc.getMimeType("test.foobar"));
	}

	/**
	 * Introduced to dispel claims in a thread on Stack Overflow:
	 * <a href="https://stackoverflow.com/questions/22986109/testing-spring-managed-servlet">Testing Spring managed servlet</a>
	 */
	@Test
	public void getMimeTypeWithCustomConfiguredType() {
		sc.addMimeType("enigma", new MediaType("text", "enigma"));
		assertEquals("text/enigma", sc.getMimeType("filename.enigma"));
	}

	@Test
	public void servletVersion() {
		assertEquals(3, sc.getMajorVersion());
		assertEquals(1, sc.getMinorVersion());
		assertEquals(3, sc.getEffectiveMajorVersion());
		assertEquals(1, sc.getEffectiveMinorVersion());

		sc.setMajorVersion(4);
		sc.setMinorVersion(0);
		sc.setEffectiveMajorVersion(4);
		sc.setEffectiveMinorVersion(0);
		assertEquals(4, sc.getMajorVersion());
		assertEquals(0, sc.getMinorVersion());
		assertEquals(4, sc.getEffectiveMajorVersion());
		assertEquals(0, sc.getEffectiveMinorVersion());
	}

	@Test
	public void registerAndUnregisterNamedDispatcher() throws Exception {
		final String name = "test-servlet";
		final String url = "/test";
		assertNull(sc.getNamedDispatcher(name));

		sc.registerNamedDispatcher(name, new MockRequestDispatcher(url));
		RequestDispatcher namedDispatcher = sc.getNamedDispatcher(name);
		assertNotNull(namedDispatcher);
		MockHttpServletResponse response = new MockHttpServletResponse();
		namedDispatcher.forward(new MockHttpServletRequest(sc), response);
		assertEquals(url, response.getForwardedUrl());

		sc.unregisterNamedDispatcher(name);
		assertNull(sc.getNamedDispatcher(name));
	}

	@Test
	public void getNamedDispatcherForDefaultServlet() throws Exception {
		final String name = "default";
		RequestDispatcher namedDispatcher = sc.getNamedDispatcher(name);
		assertNotNull(namedDispatcher);

		MockHttpServletResponse response = new MockHttpServletResponse();
		namedDispatcher.forward(new MockHttpServletRequest(sc), response);
		assertEquals(name, response.getForwardedUrl());
	}

	@Test
	public void setDefaultServletName() throws Exception {
		final String originalDefault = "default";
		final String newDefault = "test";
		assertNotNull(sc.getNamedDispatcher(originalDefault));

		sc.setDefaultServletName(newDefault);
		assertEquals(newDefault, sc.getDefaultServletName());
		assertNull(sc.getNamedDispatcher(originalDefault));

		RequestDispatcher namedDispatcher = sc.getNamedDispatcher(newDefault);
		assertNotNull(namedDispatcher);
		MockHttpServletResponse response = new MockHttpServletResponse();
		namedDispatcher.forward(new MockHttpServletRequest(sc), response);
		assertEquals(newDefault, response.getForwardedUrl());
	}

	/**
	 * @since 4.1.2
	 */
	@Test
	public void getServletRegistration() {
		assertNull(sc.getServletRegistration("servlet"));
	}

	/**
	 * @since 4.1.2
	 */
	@Test
	public void getServletRegistrations() {
		Map<String, ? extends ServletRegistration> servletRegistrations = sc.getServletRegistrations();
		assertNotNull(servletRegistrations);
		assertEquals(0, servletRegistrations.size());
	}

	/**
	 * @since 4.1.2
	 */
	@Test
	public void getFilterRegistration() {
		assertNull(sc.getFilterRegistration("filter"));
	}

	/**
	 * @since 4.1.2
	 */
	@Test
	public void getFilterRegistrations() {
		Map<String, ? extends FilterRegistration> filterRegistrations = sc.getFilterRegistrations();
		assertNotNull(filterRegistrations);
		assertEquals(0, filterRegistrations.size());
	}

}
