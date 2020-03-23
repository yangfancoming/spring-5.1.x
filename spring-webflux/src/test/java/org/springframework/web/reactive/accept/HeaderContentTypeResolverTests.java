

package org.springframework.web.reactive.accept;

import java.util.List;

import org.junit.Test;

import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.web.server.NotAcceptableStatusException;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link HeaderContentTypeResolver}.
 *
 * @author Rossen Stoyanchev
 */
public class HeaderContentTypeResolverTests {

	private final HeaderContentTypeResolver resolver = new HeaderContentTypeResolver();


	@Test
	public void resolveMediaTypes() throws Exception {
		String header = "text/plain; q=0.5, text/html, text/x-dvi; q=0.8, text/x-c";
		List<MediaType> mediaTypes = this.resolver.resolveMediaTypes(
				MockServerWebExchange.from(MockServerHttpRequest.get("/").header("accept", header)));

		assertEquals(4, mediaTypes.size());
		assertEquals("text/html", mediaTypes.get(0).toString());
		assertEquals("text/x-c", mediaTypes.get(1).toString());
		assertEquals("text/x-dvi;q=0.8", mediaTypes.get(2).toString());
		assertEquals("text/plain;q=0.5", mediaTypes.get(3).toString());
	}

	@Test(expected = NotAcceptableStatusException.class)
	public void resolveMediaTypesParseError() throws Exception {
		String header = "textplain; q=0.5";
		this.resolver.resolveMediaTypes(
				MockServerWebExchange.from(MockServerHttpRequest.get("/").header("accept", header)));
	}

}
