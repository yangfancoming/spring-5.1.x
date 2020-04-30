

package org.springframework.web.method.annotation;

import java.lang.reflect.Method;
import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;

import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import static org.junit.Assert.*;

/**
 * Test fixture with {@link org.springframework.web.method.annotation.AbstractCookieValueMethodArgumentResolver}.
 *
 * @author Arjen Poutsma
 *
 */
public class CookieValueMethodArgumentResolverTests {

	private AbstractCookieValueMethodArgumentResolver resolver;

	private MethodParameter paramNamedCookie;

	private MethodParameter paramNamedDefaultValueString;

	private MethodParameter paramString;

	private ServletWebRequest webRequest;

	private MockHttpServletRequest request;


	@Before
	public void setUp() throws Exception {
		resolver = new TestCookieValueMethodArgumentResolver();

		Method method = getClass().getMethod("params", Cookie.class, String.class, String.class);
		paramNamedCookie = new SynthesizingMethodParameter(method, 0);
		paramNamedDefaultValueString = new SynthesizingMethodParameter(method, 1);
		paramString = new SynthesizingMethodParameter(method, 2);

		request = new MockHttpServletRequest();
		webRequest = new ServletWebRequest(request, new MockHttpServletResponse());
	}


	@Test
	public void supportsParameter() {
		assertTrue("Cookie parameter not supported", resolver.supportsParameter(paramNamedCookie));
		assertTrue("Cookie string parameter not supported", resolver.supportsParameter(paramNamedDefaultValueString));
		assertFalse("non-@CookieValue parameter supported", resolver.supportsParameter(paramString));
	}

	@Test
	public void resolveCookieDefaultValue() throws Exception {
		Object result = resolver.resolveArgument(paramNamedDefaultValueString, null, webRequest, null);

		assertTrue(result instanceof String);
		assertEquals("Invalid result", "bar", result);
	}

	@Test(expected = ServletRequestBindingException.class)
	public void notFound() throws Exception {
		resolver.resolveArgument(paramNamedCookie, null, webRequest, null);
		fail("Expected exception");
	}

	private static class TestCookieValueMethodArgumentResolver extends AbstractCookieValueMethodArgumentResolver {

		public TestCookieValueMethodArgumentResolver() {
			super(null);
		}

		@Override
		protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
			return null;
		}
	}


	public void params(@CookieValue("name") Cookie param1,
			@CookieValue(name = "name", defaultValue = "bar") String param2,
			String param3) {
	}

}
