

package org.springframework.web.method.annotation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.core.MethodParameter;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

/**
 * Test fixture with {@link WebArgumentResolverAdapterTests}.
 *
 * @author Arjen Poutsma
 *
 */
public class WebArgumentResolverAdapterTests {

	private TestWebArgumentResolverAdapter adapter;

	private WebArgumentResolver adaptee;

	private MethodParameter parameter;

	private NativeWebRequest webRequest;

	@Before
	public void setUp() throws Exception {
		adaptee = mock(WebArgumentResolver.class);
		adapter = new TestWebArgumentResolverAdapter(adaptee);
		parameter = new MethodParameter(getClass().getMethod("handle", Integer.TYPE), 0);
		webRequest = new ServletWebRequest(new MockHttpServletRequest());

		// Expose request to the current thread (for SpEL expressions)
		RequestContextHolder.setRequestAttributes(webRequest);
	}

	@After
	public void resetRequestContextHolder() {
		RequestContextHolder.resetRequestAttributes();
	}

	@Test
	public void supportsParameter() throws Exception {
		given(adaptee.resolveArgument(parameter, webRequest)).willReturn(42);

		assertTrue("Parameter not supported", adapter.supportsParameter(parameter));

		verify(adaptee).resolveArgument(parameter, webRequest);
	}

	@Test
	public void supportsParameterUnresolved() throws Exception {
		given(adaptee.resolveArgument(parameter, webRequest)).willReturn(WebArgumentResolver.UNRESOLVED);

		assertFalse("Parameter supported", adapter.supportsParameter(parameter));

		verify(adaptee).resolveArgument(parameter, webRequest);
	}

	@Test
	public void supportsParameterWrongType() throws Exception {
		given(adaptee.resolveArgument(parameter, webRequest)).willReturn("Foo");

		assertFalse("Parameter supported", adapter.supportsParameter(parameter));

		verify(adaptee).resolveArgument(parameter, webRequest);
	}

	@Test
	public void supportsParameterThrowsException() throws Exception {
		given(adaptee.resolveArgument(parameter, webRequest)).willThrow(new Exception());

		assertFalse("Parameter supported", adapter.supportsParameter(parameter));

		verify(adaptee).resolveArgument(parameter, webRequest);
	}

	@Test
	public void resolveArgument() throws Exception {
		int expected = 42;
		given(adaptee.resolveArgument(parameter, webRequest)).willReturn(expected);

		Object result = adapter.resolveArgument(parameter, null, webRequest, null);
		assertEquals("Invalid result", expected, result);
	}

	@Test(expected = IllegalStateException.class)
	public void resolveArgumentUnresolved() throws Exception {
		given(adaptee.resolveArgument(parameter, webRequest)).willReturn(WebArgumentResolver.UNRESOLVED);

		adapter.resolveArgument(parameter, null, webRequest, null);
	}

	@Test(expected = IllegalStateException.class)
	public void resolveArgumentWrongType() throws Exception {
		given(adaptee.resolveArgument(parameter, webRequest)).willReturn("Foo");

		adapter.resolveArgument(parameter, null, webRequest, null);
	}

	@Test(expected = Exception.class)
	public void resolveArgumentThrowsException() throws Exception {
		given(adaptee.resolveArgument(parameter, webRequest)).willThrow(new Exception());

		adapter.resolveArgument(parameter, null, webRequest, null);
	}

	public void handle(int param) {
	}

	private class TestWebArgumentResolverAdapter extends AbstractWebArgumentResolverAdapter {

		public TestWebArgumentResolverAdapter(WebArgumentResolver adaptee) {
			super(adaptee);
		}

		@Override
		protected NativeWebRequest getWebRequest() {
			return WebArgumentResolverAdapterTests.this.webRequest;
		}
	}

}
