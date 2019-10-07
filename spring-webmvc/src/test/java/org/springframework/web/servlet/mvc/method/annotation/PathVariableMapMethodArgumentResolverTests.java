

package org.springframework.web.servlet.mvc.method.annotation;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import org.springframework.core.MethodParameter;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

import static org.junit.Assert.*;

/**
 * Test fixture with {@link PathVariableMapMethodArgumentResolver}.
 *
 * @author Rossen Stoyanchev
 */
public class PathVariableMapMethodArgumentResolverTests {

	private PathVariableMapMethodArgumentResolver resolver;

	private ModelAndViewContainer mavContainer;

	private ServletWebRequest webRequest;

	private MockHttpServletRequest request;

	private MethodParameter paramMap;
	private MethodParameter paramNamedMap;
	private MethodParameter paramMapNoAnnot;


	@Before
	public void setup() throws Exception {
		resolver = new PathVariableMapMethodArgumentResolver();
		mavContainer = new ModelAndViewContainer();
		request = new MockHttpServletRequest();
//		webRequest = new ServletWebRequest(request, new MockHttpServletResponse());

		Method method = getClass().getMethod("handle", Map.class, Map.class, Map.class);
		paramMap = new MethodParameter(method, 0);
		paramNamedMap = new MethodParameter(method, 1);
		paramMapNoAnnot = new MethodParameter(method, 2);
	}


	@Test
	public void supportsParameter() {
		assertTrue(resolver.supportsParameter(paramMap));
		assertFalse(resolver.supportsParameter(paramNamedMap));
		assertFalse(resolver.supportsParameter(paramMapNoAnnot));
	}

	@Test
	public void resolveArgument() throws Exception {
		Map<String, String> uriTemplateVars = new HashMap<>();
		uriTemplateVars.put("name1", "value1");
		uriTemplateVars.put("name2", "value2");
		request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriTemplateVars);

		Object result = resolver.resolveArgument(paramMap, mavContainer, webRequest, null);

		assertEquals(uriTemplateVars, result);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void resolveArgumentNoUriVars() throws Exception {
		Map<String, String> map = (Map<String, String>) resolver.resolveArgument(paramMap, mavContainer, webRequest, null);

		assertEquals(Collections.emptyMap(), map);
	}


	public void handle(
			@PathVariable Map<String, String> map,
			@PathVariable(value = "name") Map<String, String> namedMap,
			Map<String, String> mapWithoutAnnotat) {
	}

}
