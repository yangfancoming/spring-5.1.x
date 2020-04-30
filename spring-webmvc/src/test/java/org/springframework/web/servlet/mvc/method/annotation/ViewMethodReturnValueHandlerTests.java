

package org.springframework.web.servlet.mvc.method.annotation;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import org.springframework.core.MethodParameter;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.RedirectView;

import static org.junit.Assert.*;

/**
 * Test fixture with {@link ViewMethodReturnValueHandler}.
 *
 *
 */
public class ViewMethodReturnValueHandlerTests {

	private ViewMethodReturnValueHandler handler;

	private ModelAndViewContainer mavContainer;

	private ServletWebRequest webRequest;


	@Before
	public void setup() {
		this.handler = new ViewMethodReturnValueHandler();
		this.mavContainer = new ModelAndViewContainer();
		this.webRequest = new ServletWebRequest(new MockHttpServletRequest());
	}


	@Test
	public void supportsReturnType() throws Exception {
		assertTrue(this.handler.supportsReturnType(createReturnValueParam("view")));
	}

	@Test
	public void returnView() throws Exception {
		InternalResourceView view = new InternalResourceView("testView");
		this.handler.handleReturnValue(view, createReturnValueParam("view"), this.mavContainer, this.webRequest);

		assertSame(view, this.mavContainer.getView());
	}

	@Test
	public void returnViewRedirect() throws Exception {
		RedirectView redirectView = new RedirectView("testView");
		ModelMap redirectModel = new RedirectAttributesModelMap();
		this.mavContainer.setRedirectModel(redirectModel);
		MethodParameter param = createReturnValueParam("view");
		this.handler.handleReturnValue(redirectView, param, this.mavContainer, this.webRequest);

		assertSame(redirectView, this.mavContainer.getView());
		assertSame("Should have switched to the RedirectModel", redirectModel, this.mavContainer.getModel());
	}

	private MethodParameter createReturnValueParam(String methodName) throws Exception {
		Method method = getClass().getDeclaredMethod(methodName);
		return new MethodParameter(method, -1);
	}


	View view() {
		return null;
	}

}
