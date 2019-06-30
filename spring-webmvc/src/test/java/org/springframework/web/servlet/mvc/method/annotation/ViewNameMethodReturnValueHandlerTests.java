

package org.springframework.web.servlet.mvc.method.annotation;

import org.junit.Before;
import org.junit.Test;

import org.springframework.core.MethodParameter;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static org.junit.Assert.*;

/**
 * Test fixture with {@link ViewNameMethodReturnValueHandler}.
 *
 * @author Rossen Stoyanchev
 */
public class ViewNameMethodReturnValueHandlerTests {

	private ViewNameMethodReturnValueHandler handler;

	private ModelAndViewContainer mavContainer;

	private ServletWebRequest webRequest;

	private MethodParameter param;


	@Before
	public void setup() throws NoSuchMethodException {
		this.handler = new ViewNameMethodReturnValueHandler();
		this.mavContainer = new ModelAndViewContainer();
		this.webRequest = new ServletWebRequest(new MockHttpServletRequest());

		this.param = new MethodParameter(getClass().getDeclaredMethod("viewName"), -1);
	}


	@Test
	public void supportsReturnType() throws Exception {
		assertTrue(this.handler.supportsReturnType(this.param));
	}

	@Test
	public void returnViewName() throws Exception {
		this.handler.handleReturnValue("testView", this.param, this.mavContainer, this.webRequest);
		assertEquals("testView", this.mavContainer.getViewName());
	}

	@Test
	public void returnViewNameRedirect() throws Exception {
		ModelMap redirectModel = new RedirectAttributesModelMap();
		this.mavContainer.setRedirectModel(redirectModel);
		this.handler.handleReturnValue("redirect:testView", this.param, this.mavContainer, this.webRequest);
		assertEquals("redirect:testView", this.mavContainer.getViewName());
		assertSame(redirectModel, this.mavContainer.getModel());
	}

	@Test
	public void returnViewCustomRedirect() throws Exception {
		ModelMap redirectModel = new RedirectAttributesModelMap();
		this.mavContainer.setRedirectModel(redirectModel);
		this.handler.setRedirectPatterns("myRedirect:*");
		this.handler.handleReturnValue("myRedirect:testView", this.param, this.mavContainer, this.webRequest);
		assertEquals("myRedirect:testView", this.mavContainer.getViewName());
		assertSame(redirectModel, this.mavContainer.getModel());
	}

	@Test
	public void returnViewRedirectWithCustomRedirectPattern() throws Exception {
		ModelMap redirectModel = new RedirectAttributesModelMap();
		this.mavContainer.setRedirectModel(redirectModel);
		this.handler.setRedirectPatterns("myRedirect:*");
		this.handler.handleReturnValue("redirect:testView", this.param, this.mavContainer, this.webRequest);
		assertEquals("redirect:testView", this.mavContainer.getViewName());
		assertSame(redirectModel, this.mavContainer.getModel());
	}


	@SuppressWarnings("unused")
	String viewName() {
		return null;
	}

}