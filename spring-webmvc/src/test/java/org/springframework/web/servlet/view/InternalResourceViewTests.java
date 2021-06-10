

package org.springframework.web.servlet.view;

import org.junit.Test;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.mock.web.test.MockRequestDispatcher;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.web.servlet.View;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.*;

/**
 * Unit tests for {@link InternalResourceView}.
 *
 * @author Rod Johnson

 * @author Sam Brannen
 */
public class InternalResourceViewTests {

	@SuppressWarnings("serial")
	private static final Map<String, Object> model = Collections.unmodifiableMap(new HashMap<String, Object>() {{
		put("foo", "bar");
		put("I", 1L);
	}});

	private static final String url = "forward-to";

	private final HttpServletRequest request = mock(HttpServletRequest.class);

	private final MockHttpServletResponse response = new MockHttpServletResponse();

	private final InternalResourceView view = new InternalResourceView();


	/**
	 * If the url property isn't supplied, view initialization should fail.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void rejectsNullUrl() throws Exception {
		view.afterPropertiesSet();
	}

	@Test
	public void forward() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/myservlet/handler.do");
		request.setContextPath("/mycontext");
		request.setServletPath("/myservlet");
		request.setPathInfo(";mypathinfo");
		request.setQueryString("?param1=value1");

		view.setUrl(url);
		view.setServletContext(new MockServletContext() {
			@Override
			public int getMinorVersion() {
				return 4;
			}
		});

		view.render(model, request, response);
		assertEquals(url, response.getForwardedUrl());

		model.forEach((key, value) -> assertEquals("Values for model key '" + key
				+ "' must match", value, request.getAttribute(key)));
	}

	@Test
	public void alwaysInclude() throws Exception {
		given(request.getAttribute(View.PATH_VARIABLES)).willReturn(null);
		given(request.getRequestDispatcher(url)).willReturn(new MockRequestDispatcher(url));

		view.setUrl(url);
		view.setAlwaysInclude(true);

		// Can now try multiple tests
		view.render(model, request, response);
		assertEquals(url, response.getIncludedUrl());

		model.forEach((key, value) -> verify(request).setAttribute(key, value));
	}

	@Test
	public void includeOnAttribute() throws Exception {
		given(request.getAttribute(View.PATH_VARIABLES)).willReturn(null);
		given(request.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE)).willReturn("somepath");
		given(request.getRequestDispatcher(url)).willReturn(new MockRequestDispatcher(url));

		view.setUrl(url);

		// Can now try multiple tests
		view.render(model, request, response);
		assertEquals(url, response.getIncludedUrl());

		model.forEach((key, value) -> verify(request).setAttribute(key, value));
	}

	@Test
	public void includeOnCommitted() throws Exception {
		given(request.getAttribute(View.PATH_VARIABLES)).willReturn(null);
		given(request.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE)).willReturn(null);
		given(request.getRequestDispatcher(url)).willReturn(new MockRequestDispatcher(url));

		response.setCommitted(true);
		view.setUrl(url);

		// Can now try multiple tests
		view.render(model, request, response);
		assertEquals(url, response.getIncludedUrl());

		model.forEach((k, v) -> verify(request).setAttribute(k, v));
	}

}
