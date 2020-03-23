
package org.springframework.web.reactive.accept;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.server.ServerWebExchange;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link ParameterContentTypeResolver}.
 * @author Rossen Stoyanchev
 */
public class ParameterContentTypeResolverTests {

	@Test
	public void noKey() {
		ParameterContentTypeResolver resolver = new ParameterContentTypeResolver(Collections.emptyMap());
		ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));
		List<MediaType> mediaTypes = resolver.resolveMediaTypes(exchange);

		assertEquals(RequestedContentTypeResolver.MEDIA_TYPE_ALL_LIST, mediaTypes);
	}

	@Test(expected = NotAcceptableStatusException.class)
	public void noMatchForKey() {
		ParameterContentTypeResolver resolver = new ParameterContentTypeResolver(Collections.emptyMap());
		List<MediaType> mediaTypes = resolver.resolveMediaTypes(createExchange("blah"));

		assertEquals(0, mediaTypes.size());
	}

	@Test
	public void resolveKeyFromRegistrations() {
		ServerWebExchange exchange = createExchange("html");

		Map<String, MediaType> mapping = Collections.emptyMap();
		RequestedContentTypeResolver resolver = new ParameterContentTypeResolver(mapping);
		List<MediaType> mediaTypes = resolver.resolveMediaTypes(exchange);
		assertEquals(Collections.singletonList(new MediaType("text", "html")), mediaTypes);

		mapping = Collections.singletonMap("HTML", MediaType.APPLICATION_XHTML_XML);
		resolver = new ParameterContentTypeResolver(mapping);
		mediaTypes = resolver.resolveMediaTypes(exchange);
		assertEquals(Collections.singletonList(new MediaType("application", "xhtml+xml")), mediaTypes);
	}

	@Test
	public void resolveKeyThroughMediaTypeFactory() {
		ServerWebExchange exchange = createExchange("xls");
		RequestedContentTypeResolver resolver = new ParameterContentTypeResolver(Collections.emptyMap());
		List<MediaType> mediaTypes = resolver.resolveMediaTypes(exchange);

		assertEquals(Collections.singletonList(new MediaType("application", "vnd.ms-excel")), mediaTypes);
	}

	@Test // SPR-13747
	public void resolveKeyIsCaseInsensitive() {
		ServerWebExchange exchange = createExchange("JSoN");
		Map<String, MediaType> mapping = Collections.singletonMap("json", MediaType.APPLICATION_JSON);
		ParameterContentTypeResolver resolver = new ParameterContentTypeResolver(mapping);
		List<MediaType> mediaTypes = resolver.resolveMediaTypes(exchange);

		assertEquals(Collections.singletonList(MediaType.APPLICATION_JSON), mediaTypes);
	}

	private MockServerWebExchange createExchange(String format) {
		return MockServerWebExchange.from(MockServerHttpRequest.get("/path?format=" + format));
	}

}
