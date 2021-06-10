

package org.springframework.web.servlet.mvc;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.Assert.*;

/**
 * Test fixture with a ParameterizableViewController.
 *
 *
 * @since 3.1.1
 */
public class ParameterizableViewControllerTests {

	private ParameterizableViewController controller;

	private MockHttpServletRequest request;

	@Before
	public void setup() {
		this.controller = new ParameterizableViewController();
		this.request = new MockHttpServletRequest("GET", "/");
	}

	@Test
	public void handleRequestWithViewName() throws Exception {
		String viewName = "testView";
		this.controller.setViewName(viewName);
		ModelAndView mav = this.controller.handleRequest(this.request, new MockHttpServletResponse());
		assertEquals(viewName, mav.getViewName());
		assertTrue(mav.getModel().isEmpty());
	}

	@Test
	public void handleRequestWithoutViewName() throws Exception {
		ModelAndView mav = this.controller.handleRequest(this.request, new MockHttpServletResponse());
		assertNull(mav.getViewName());
		assertTrue(mav.getModel().isEmpty());
	}

	@Test
	public void handleRequestWithFlashAttributes() throws Exception {
		this.request.setAttribute(DispatcherServlet.INPUT_FLASH_MAP_ATTRIBUTE, new ModelMap("name", "value"));
		ModelAndView mav = this.controller.handleRequest(this.request, new MockHttpServletResponse());
		assertEquals(1, mav.getModel().size());
		assertEquals("value", mav.getModel().get("name"));
	}

	@Test
	public void handleRequestHttpOptions() throws Exception {
		this.request.setMethod(HttpMethod.OPTIONS.name());
		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelAndView mav = this.controller.handleRequest(this.request, response);

		assertNull(mav);
		assertEquals("GET,HEAD,OPTIONS", response.getHeader("Allow"));
	}

}
