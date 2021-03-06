

package org.springframework.test.web.servlet.result;

import org.hamcrest.Matchers;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.StubMvcResult;

/**
 *
 */
public class ContentResultMatchersTests {

	@Test
	public void typeMatches() throws Exception {
		new ContentResultMatchers().contentType("application/json;charset=UTF-8").match(getStubMvcResult());
	}

	@Test(expected = AssertionError.class)
	public void typeNoMatch() throws Exception {
		new ContentResultMatchers().contentType("text/plain").match(getStubMvcResult());
	}

	@Test
	public void encoding() throws Exception {
		new ContentResultMatchers().encoding("UTF-8").match(getStubMvcResult());
	}

	@Test(expected = AssertionError.class)
	public void encodingNoMatch() throws Exception {
		new ContentResultMatchers().encoding("ISO-8859-1").match(getStubMvcResult());
	}

	@Test
	public void string() throws Exception {
		new ContentResultMatchers().string(new String(CONTENT.getBytes("UTF-8"))).match(getStubMvcResult());
	}

	@Test(expected = AssertionError.class)
	public void stringNoMatch() throws Exception {
		new ContentResultMatchers().encoding("bogus").match(getStubMvcResult());
	}

	@Test
	public void stringMatcher() throws Exception {
		String content = new String(CONTENT.getBytes("UTF-8"));
		new ContentResultMatchers().string(Matchers.equalTo(content)).match(getStubMvcResult());
	}

	@Test(expected = AssertionError.class)
	public void stringMatcherNoMatch() throws Exception {
		new ContentResultMatchers().string(Matchers.equalTo("bogus")).match(getStubMvcResult());
	}

	@Test
	public void bytes() throws Exception {
		new ContentResultMatchers().bytes(CONTENT.getBytes("UTF-8")).match(getStubMvcResult());
	}

	@Test(expected = AssertionError.class)
	public void bytesNoMatch() throws Exception {
		new ContentResultMatchers().bytes("bogus".getBytes()).match(getStubMvcResult());
	}

	@Test
	public void jsonLenientMatch() throws Exception {
		new ContentResultMatchers().json("{\n \"foo\" : \"bar\"  \n}").match(getStubMvcResult());
		new ContentResultMatchers().json("{\n \"foo\" : \"bar\"  \n}", false).match(getStubMvcResult());
	}

	@Test
	public void jsonStrictMatch() throws Exception {
		new ContentResultMatchers().json("{\n \"foo\":\"bar\",   \"foo array\":[\"foo\",\"bar\"] \n}", true).match(getStubMvcResult());
		new ContentResultMatchers().json("{\n \"foo array\":[\"foo\",\"bar\"], \"foo\":\"bar\" \n}", true).match(getStubMvcResult());
	}

	@Test(expected = AssertionError.class)
	public void jsonLenientNoMatch() throws Exception {
		new ContentResultMatchers().json("{\n\"fooo\":\"bar\"\n}").match(getStubMvcResult());
	}

	@Test(expected = AssertionError.class)
	public void jsonStrictNoMatch() throws Exception {
		new ContentResultMatchers().json("{\"foo\":\"bar\",   \"foo array\":[\"bar\",\"foo\"]}", true).match(getStubMvcResult());
	}

	private static final String CONTENT = "{\"foo\":\"bar\",\"foo array\":[\"foo\",\"bar\"]}";

	private StubMvcResult getStubMvcResult() throws Exception {
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.addHeader("Content-Type", "application/json; charset=UTF-8");
		response.getWriter().print(new String(CONTENT.getBytes("UTF-8")));
		return new StubMvcResult(null, null, null, null, null, null, response);
	}

}
