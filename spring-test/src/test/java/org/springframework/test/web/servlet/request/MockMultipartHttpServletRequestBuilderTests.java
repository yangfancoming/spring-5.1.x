

package org.springframework.test.web.servlet.request;

import org.junit.Test;

import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;

import static org.junit.Assert.*;

/**
 *
 */
public class MockMultipartHttpServletRequestBuilderTests {

	@Test
	public void test() {
		MockHttpServletRequestBuilder parent = new MockHttpServletRequestBuilder(HttpMethod.GET, "/");
		parent.characterEncoding("UTF-8");
		Object result = new MockMultipartHttpServletRequestBuilder("/fileUpload").merge(parent);

		assertNotNull(result);
		assertEquals(MockMultipartHttpServletRequestBuilder.class, result.getClass());

		MockMultipartHttpServletRequestBuilder builder = (MockMultipartHttpServletRequestBuilder) result;
		MockHttpServletRequest request = builder.buildRequest(new MockServletContext());
		assertEquals("UTF-8", request.getCharacterEncoding());
	}

}
