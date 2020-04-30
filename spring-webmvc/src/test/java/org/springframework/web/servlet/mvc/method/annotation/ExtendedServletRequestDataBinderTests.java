

package org.springframework.web.servlet.mvc.method.annotation;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.HandlerMapping;

import static org.junit.Assert.*;

/**
 * Test fixture for {@link ExtendedServletRequestDataBinder}.
 *
 *
 */
public class ExtendedServletRequestDataBinderTests {

	private MockHttpServletRequest request;

	@Before
	public void setup() {
		this.request = new MockHttpServletRequest();
	}

	@Test
	public void createBinder() throws Exception {
		Map<String, String> uriTemplateVars = new HashMap<>();
		uriTemplateVars.put("name", "nameValue");
		uriTemplateVars.put("age", "25");
		request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriTemplateVars);

		TestBean target = new TestBean();
		WebDataBinder binder = new ExtendedServletRequestDataBinder(target, "");
		((ServletRequestDataBinder) binder).bind(request);

		assertEquals("nameValue", target.getName());
		assertEquals(25, target.getAge());
	}

	@Test
	public void uriTemplateVarAndRequestParam() throws Exception {
		request.addParameter("age", "35");

		Map<String, String> uriTemplateVars = new HashMap<>();
		uriTemplateVars.put("name", "nameValue");
		uriTemplateVars.put("age", "25");
		request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriTemplateVars);

		TestBean target = new TestBean();
		WebDataBinder binder = new ExtendedServletRequestDataBinder(target, "");
		((ServletRequestDataBinder) binder).bind(request);

		assertEquals("nameValue", target.getName());
		assertEquals(35, target.getAge());
	}

	@Test
	public void noUriTemplateVars() throws Exception {
		TestBean target = new TestBean();
		WebDataBinder binder = new ExtendedServletRequestDataBinder(target, "");
		((ServletRequestDataBinder) binder).bind(request);

		assertEquals(null, target.getName());
		assertEquals(0, target.getAge());
	}

}
