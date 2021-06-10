

package org.springframework.web.servlet.mvc.method.annotation;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.Cookie;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

/**
 * Test fixture with {@link ServletCookieValueMethodArgumentResolver}.
 *
 * @author Arjen Poutsma
 *
 */
public class ServletCookieValueMethodArgumentResolverTests {

	private ServletCookieValueMethodArgumentResolver resolver;

	private MockHttpServletRequest request;

	private ServletWebRequest webRequest;

	private MethodParameter cookieParameter;
	private MethodParameter cookieStringParameter;


	@Before
	public void setup() throws Exception {
		resolver = new ServletCookieValueMethodArgumentResolver(null);
		request = new MockHttpServletRequest();
		webRequest = new ServletWebRequest(request, new MockHttpServletResponse());

		Method method = getClass().getMethod("params", Cookie.class, String.class);
		cookieParameter = new SynthesizingMethodParameter(method, 0);
		cookieStringParameter = new SynthesizingMethodParameter(method, 1);
	}


	@Test
	public void resolveCookieArgument() throws Exception {
		Cookie expected = new Cookie("name", "foo");
		request.setCookies(expected);

		Cookie result = (Cookie) resolver.resolveArgument(cookieParameter, null, webRequest, null);
		assertEquals("Invalid result", expected, result);
	}

	@Test
	public void resolveCookieStringArgument() throws Exception {
		Cookie cookie = new Cookie("name", "foo");
		request.setCookies(cookie);

		String result = (String) resolver.resolveArgument(cookieStringParameter, null, webRequest, null);
		assertEquals("Invalid result", cookie.getValue(), result);
	}


	public void params(@CookieValue("name") Cookie cookie,
			@CookieValue(name = "name", defaultValue = "bar") String cookieString) {
	}

}
