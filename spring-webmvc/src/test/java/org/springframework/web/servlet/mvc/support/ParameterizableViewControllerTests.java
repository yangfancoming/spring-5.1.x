

package org.springframework.web.servlet.mvc.support;

import org.junit.Test;

import org.springframework.http.HttpStatus;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.view.RedirectView;

import static org.junit.Assert.*;

/**
 * Unit tests for
 * {@link org.springframework.web.servlet.mvc.ParameterizableViewController}.
 *
 *
 * @since 4.1
 */
public class ParameterizableViewControllerTests {

	private final ParameterizableViewController controller = new ParameterizableViewController();

	private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");

	private final MockHttpServletResponse response = new MockHttpServletResponse();


	@Test
	public void defaultViewName() throws Exception {
		ModelAndView modelAndView = this.controller.handleRequest(this.request, this.response);
		assertNull(modelAndView.getViewName());
	}

	@Test
	public void viewName() throws Exception {
		this.controller.setViewName("view");
		ModelAndView modelAndView = this.controller.handleRequest(this.request, this.response);
		assertEquals("view", modelAndView.getViewName());
	}

	@Test
	public void viewNameAndStatus() throws Exception {
		this.controller.setViewName("view");
		this.controller.setStatusCode(HttpStatus.NOT_FOUND);
		ModelAndView modelAndView = this.controller.handleRequest(this.request, this.response);
		assertEquals("view", modelAndView.getViewName());
		assertEquals(404, this.response.getStatus());
	}

	@Test
	public void viewNameAndStatus204() throws Exception {
		this.controller.setStatusCode(HttpStatus.NO_CONTENT);
		ModelAndView modelAndView = this.controller.handleRequest(this.request, this.response);
		assertNull(modelAndView);
		assertEquals(204, this.response.getStatus());
	}

	@Test
	public void redirectStatus() throws Exception {
		this.controller.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
		this.controller.setViewName("/foo");
		ModelAndView modelAndView = this.controller.handleRequest(this.request, this.response);

		assertEquals("redirect:/foo", modelAndView.getViewName());
		assertEquals("3xx status should be left to RedirectView to set", 200, this.response.getStatus());
		assertEquals(HttpStatus.PERMANENT_REDIRECT, this.request.getAttribute(View.RESPONSE_STATUS_ATTRIBUTE));
	}

	@Test
	public void redirectStatusWithRedirectPrefix() throws Exception {
		this.controller.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
		this.controller.setViewName("redirect:/foo");
		ModelAndView modelAndView = this.controller.handleRequest(this.request, this.response);

		assertEquals("redirect:/foo", modelAndView.getViewName());
		assertEquals("3xx status should be left to RedirectView to set", 200, this.response.getStatus());
		assertEquals(HttpStatus.PERMANENT_REDIRECT, this.request.getAttribute(View.RESPONSE_STATUS_ATTRIBUTE));
	}

	@Test
	public void redirectView() throws Exception {
		RedirectView view = new RedirectView("/foo");
		this.controller.setView(view);
		ModelAndView modelAndView = this.controller.handleRequest(this.request, this.response);
		assertSame(view, modelAndView.getView());
	}

	@Test
	public void statusOnly() throws Exception {
		this.controller.setStatusCode(HttpStatus.NOT_FOUND);
		this.controller.setStatusOnly(true);
		ModelAndView modelAndView = this.controller.handleRequest(this.request, this.response);
		assertNull(modelAndView);
		assertEquals(404, this.response.getStatus());
	}

}
