

package org.springframework.web.servlet.support;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.util.WebUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class RequestContextTests {

	private MockHttpServletRequest request = new MockHttpServletRequest();

	private MockHttpServletResponse response = new MockHttpServletResponse();

	private MockServletContext servletContext = new MockServletContext();

	private Map<String, Object> model = new HashMap<>();

	@Before
	public void init() {
		GenericWebApplicationContext applicationContext = new GenericWebApplicationContext();
		applicationContext.refresh();
		servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);
	}

	@Test
	public void testGetContextUrl()  {
		request.setContextPath("foo/");
		RequestContext context = new RequestContext(request, response, servletContext, model);
		assertEquals("foo/bar", context.getContextUrl("bar"));
	}

	@Test
	public void testGetContextUrlWithMap()  {
		request.setContextPath("foo/");
		RequestContext context = new RequestContext(request, response, servletContext, model);
		Map<String, Object> map = new HashMap<>();
		map.put("foo", "bar");
		map.put("spam", "bucket");
		assertEquals("foo/bar?spam=bucket", context.getContextUrl("{foo}?spam={spam}", map));
	}

	@Test
	public void testGetContextUrlWithMapEscaping()  {
		request.setContextPath("foo/");
		RequestContext context = new RequestContext(request, response, servletContext, model);
		Map<String, Object> map = new HashMap<>();
		map.put("foo", "bar baz");
		map.put("spam", "&bucket=");
		assertEquals("foo/bar%20baz?spam=%26bucket%3D", context.getContextUrl("{foo}?spam={spam}", map));
	}

	@Test
	public void testPathToServlet()  {
		request.setContextPath("/app");
		request.setServletPath("/servlet");
		RequestContext context = new RequestContext(request, response, servletContext, model);

		assertEquals("/app/servlet", context.getPathToServlet());

		request.setAttribute(WebUtils.FORWARD_CONTEXT_PATH_ATTRIBUTE, "/origApp");
		request.setAttribute(WebUtils.FORWARD_SERVLET_PATH_ATTRIBUTE, "/origServlet");

		assertEquals("/origApp/origServlet", context.getPathToServlet());
	}

}
