

package org.springframework.web.server.adapter;

import java.net.URI;

import org.junit.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link ForwardedHeaderTransformer}.
 *
 */
public class ForwardedHeaderTransformerTests {

	private static final String BASE_URL = "https://example.com/path";


	private final ForwardedHeaderTransformer requestMutator = new ForwardedHeaderTransformer();


	@Test
	public void removeOnly() {

		this.requestMutator.setRemoveOnly(true);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Forwarded", "for=192.0.2.60;proto=http;by=203.0.113.43");
		headers.add("X-Forwarded-Host", "example.com");
		headers.add("X-Forwarded-Port", "8080");
		headers.add("X-Forwarded-Proto", "http");
		headers.add("X-Forwarded-Prefix", "prefix");
		headers.add("X-Forwarded-Ssl", "on");
		ServerHttpRequest request = this.requestMutator.apply(getRequest(headers));

		assertForwardedHeadersRemoved(request);
	}

	@Test
	public void xForwardedHeaders() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Forwarded-Host", "84.198.58.199");
		headers.add("X-Forwarded-Port", "443");
		headers.add("X-Forwarded-Proto", "https");
		headers.add("foo", "bar");
		ServerHttpRequest request = this.requestMutator.apply(getRequest(headers));

		assertEquals(new URI("https://84.198.58.199/path"), request.getURI());
		assertForwardedHeadersRemoved(request);
	}

	@Test
	public void forwardedHeader() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Forwarded", "host=84.198.58.199;proto=https");
		ServerHttpRequest request = this.requestMutator.apply(getRequest(headers));

		assertEquals(new URI("https://84.198.58.199/path"), request.getURI());
		assertForwardedHeadersRemoved(request);
	}

	@Test
	public void xForwardedPrefix() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Forwarded-Prefix", "/prefix");
		ServerHttpRequest request = this.requestMutator.apply(getRequest(headers));

		assertEquals(new URI("https://example.com/prefix/path"), request.getURI());
		assertEquals("/prefix/path", request.getPath().value());
		assertForwardedHeadersRemoved(request);
	}

	@Test
	public void xForwardedPrefixTrailingSlash() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Forwarded-Prefix", "/prefix////");
		ServerHttpRequest request = this.requestMutator.apply(getRequest(headers));

		assertEquals(new URI("https://example.com/prefix/path"), request.getURI());
		assertEquals("/prefix/path", request.getPath().value());
		assertForwardedHeadersRemoved(request);
	}

	@Test // SPR-17525
	public void shouldNotDoubleEncode() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Forwarded", "host=84.198.58.199;proto=https");

		ServerHttpRequest request = MockServerHttpRequest
				.method(HttpMethod.GET, new URI("https://example.com/a%20b?q=a%2Bb"))
				.headers(headers)
				.build();

		request = this.requestMutator.apply(request);

		assertEquals(new URI("https://84.198.58.199/a%20b?q=a%2Bb"), request.getURI());
		assertForwardedHeadersRemoved(request);
	}


	private MockServerHttpRequest getRequest(HttpHeaders headers) {
		return MockServerHttpRequest.get(BASE_URL).headers(headers).build();
	}

	private void assertForwardedHeadersRemoved(ServerHttpRequest request) {
		ForwardedHeaderTransformer.FORWARDED_HEADER_NAMES
				.forEach(name -> assertFalse(request.getHeaders().containsKey(name)));
	}

}
