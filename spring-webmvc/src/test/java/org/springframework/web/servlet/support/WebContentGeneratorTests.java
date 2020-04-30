
package org.springframework.web.servlet.support;

import java.util.Arrays;

import org.junit.Test;

import org.springframework.mock.web.test.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit tests for {@link WebContentGenerator}.
 *
 */
public class WebContentGeneratorTests {

	@Test
	public void getAllowHeaderWithConstructorTrue() throws Exception {
		WebContentGenerator generator = new TestWebContentGenerator(true);
		assertEquals("GET,HEAD,POST,OPTIONS", generator.getAllowHeader());
	}

	@Test
	public void getAllowHeaderWithConstructorFalse() throws Exception {
		WebContentGenerator generator = new TestWebContentGenerator(false);
		assertEquals("GET,HEAD,POST,PUT,PATCH,DELETE,OPTIONS", generator.getAllowHeader());
	}

	@Test
	public void getAllowHeaderWithSupportedMethodsConstructor() throws Exception {
		WebContentGenerator generator = new TestWebContentGenerator("POST");
		assertEquals("POST,OPTIONS", generator.getAllowHeader());
	}

	@Test
	public void getAllowHeaderWithSupportedMethodsSetter() throws Exception {
		WebContentGenerator generator = new TestWebContentGenerator();
		generator.setSupportedMethods("POST");
		assertEquals("POST,OPTIONS", generator.getAllowHeader());
	}

	@Test
	public void getAllowHeaderWithSupportedMethodsSetterEmpty() throws Exception {
		WebContentGenerator generator = new TestWebContentGenerator();
		generator.setSupportedMethods();
		assertEquals("Effectively \"no restriction\" on supported methods",
				"GET,HEAD,POST,PUT,PATCH,DELETE,OPTIONS", generator.getAllowHeader());
	}

	@Test
	public void varyHeaderNone() throws Exception {
		WebContentGenerator generator = new TestWebContentGenerator();
		MockHttpServletResponse response = new MockHttpServletResponse();
		generator.prepareResponse(response);

		assertNull(response.getHeader("Vary"));
	}

	@Test
	public void varyHeader() throws Exception {
		String[] configuredValues = {"Accept-Language", "User-Agent"};
		String[] responseValues = {};
		String[] expected = {"Accept-Language", "User-Agent"};
		testVaryHeader(configuredValues, responseValues, expected);
	}

	@Test
	public void varyHeaderWithExistingWildcard() throws Exception {
		String[] configuredValues = {"Accept-Language"};
		String[] responseValues = {"*"};
		String[] expected = {"*"};
		testVaryHeader(configuredValues, responseValues, expected);
	}

	@Test
	public void varyHeaderWithExistingCommaValues() throws Exception {
		String[] configuredValues = {"Accept-Language", "User-Agent"};
		String[] responseValues = {"Accept-Encoding", "Accept-Language"};
		String[] expected = {"Accept-Encoding", "Accept-Language", "User-Agent"};
		testVaryHeader(configuredValues, responseValues, expected);
	}

	@Test
	public void varyHeaderWithExistingCommaSeparatedValues() throws Exception {
		String[] configuredValues = {"Accept-Language", "User-Agent"};
		String[] responseValues = {"Accept-Encoding, Accept-Language"};
		String[] expected = {"Accept-Encoding, Accept-Language", "User-Agent"};
		testVaryHeader(configuredValues, responseValues, expected);
	}

	private void testVaryHeader(String[] configuredValues, String[] responseValues, String[] expected) {
		WebContentGenerator generator = new TestWebContentGenerator();
		generator.setVaryByRequestHeaders(configuredValues);
		MockHttpServletResponse response = new MockHttpServletResponse();
		for (String value : responseValues) {
			response.addHeader("Vary", value);
		}
		generator.prepareResponse(response);
		assertEquals(Arrays.asList(expected), response.getHeaderValues("Vary"));
	}


	private static class TestWebContentGenerator extends WebContentGenerator {

		public TestWebContentGenerator() {
		}

		public TestWebContentGenerator(boolean restrictDefaultSupportedMethods) {
			super(restrictDefaultSupportedMethods);
		}

		public TestWebContentGenerator(String... supportedMethods) {
			super(supportedMethods);
		}
	}
}
