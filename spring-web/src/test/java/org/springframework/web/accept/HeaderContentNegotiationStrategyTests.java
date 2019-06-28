

package org.springframework.web.accept;

import java.util.List;

import org.junit.Test;

import org.springframework.http.MediaType;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import static org.junit.Assert.*;

/**
 * Test fixture for HeaderContentNegotiationStrategy tests.
 *
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 */
public class HeaderContentNegotiationStrategyTests {

	private final HeaderContentNegotiationStrategy strategy = new HeaderContentNegotiationStrategy();

	private final MockHttpServletRequest servletRequest = new MockHttpServletRequest();

	private final NativeWebRequest webRequest = new ServletWebRequest(this.servletRequest);


	@Test
	public void resolveMediaTypes() throws Exception {
		this.servletRequest.addHeader("Accept", "text/plain; q=0.5, text/html, text/x-dvi; q=0.8, text/x-c");
		List<MediaType> mediaTypes = this.strategy.resolveMediaTypes(this.webRequest);

		assertEquals(4, mediaTypes.size());
		assertEquals("text/html", mediaTypes.get(0).toString());
		assertEquals("text/x-c", mediaTypes.get(1).toString());
		assertEquals("text/x-dvi;q=0.8", mediaTypes.get(2).toString());
		assertEquals("text/plain;q=0.5", mediaTypes.get(3).toString());
	}

	@Test  // SPR-14506
	public void resolveMediaTypesFromMultipleHeaderValues() throws Exception {
		this.servletRequest.addHeader("Accept", "text/plain; q=0.5, text/html");
		this.servletRequest.addHeader("Accept", "text/x-dvi; q=0.8, text/x-c");
		List<MediaType> mediaTypes = this.strategy.resolveMediaTypes(this.webRequest);

		assertEquals(4, mediaTypes.size());
		assertEquals("text/html", mediaTypes.get(0).toString());
		assertEquals("text/x-c", mediaTypes.get(1).toString());
		assertEquals("text/x-dvi;q=0.8", mediaTypes.get(2).toString());
		assertEquals("text/plain;q=0.5", mediaTypes.get(3).toString());
	}

	@Test(expected = HttpMediaTypeNotAcceptableException.class)
	public void resolveMediaTypesParseError() throws Exception {
		this.servletRequest.addHeader("Accept", "textplain; q=0.5");
		this.strategy.resolveMediaTypes(this.webRequest);
	}

}
