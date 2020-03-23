

package org.springframework.web.reactive.function.server;

import java.net.URI;
import java.util.Collections;
import java.util.function.Function;

import org.junit.Test;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.util.pattern.PathPatternParser;

import static org.junit.Assert.*;


public class RequestPredicatesTests {

	@Test
	public void all() {
		RequestPredicate predicate = RequestPredicates.all();
		MockServerRequest request = MockServerRequest.builder().build();
		assertTrue(predicate.test(request));
	}

	@Test
	public void method() {
		HttpMethod httpMethod = HttpMethod.GET;
		RequestPredicate predicate = RequestPredicates.method(httpMethod);
		MockServerRequest request = MockServerRequest.builder().method(httpMethod).build();
		assertTrue(predicate.test(request));

		request = MockServerRequest.builder().method(HttpMethod.POST).build();
		assertFalse(predicate.test(request));
	}

	@Test
	public void methods() {
		RequestPredicate predicate = RequestPredicates.methods(HttpMethod.GET, HttpMethod.HEAD);
		MockServerRequest request = MockServerRequest.builder().method(HttpMethod.GET).build();
		assertTrue(predicate.test(request));

		request = MockServerRequest.builder().method(HttpMethod.HEAD).build();
		assertTrue(predicate.test(request));

		request = MockServerRequest.builder().method(HttpMethod.POST).build();
		assertFalse(predicate.test(request));
	}

	@Test
	public void allMethods() {
		URI uri = URI.create("http://localhost/path");

		RequestPredicate predicate = RequestPredicates.GET("/p*");
		MockServerRequest request = MockServerRequest.builder().method(HttpMethod.GET).uri(uri).build();
		assertTrue(predicate.test(request));

		predicate = RequestPredicates.HEAD("/p*");
		request = MockServerRequest.builder().method(HttpMethod.HEAD).uri(uri).build();
		assertTrue(predicate.test(request));

		predicate = RequestPredicates.POST("/p*");
		request = MockServerRequest.builder().method(HttpMethod.POST).uri(uri).build();
		assertTrue(predicate.test(request));

		predicate = RequestPredicates.PUT("/p*");
		request = MockServerRequest.builder().method(HttpMethod.PUT).uri(uri).build();
		assertTrue(predicate.test(request));

		predicate = RequestPredicates.PATCH("/p*");
		request = MockServerRequest.builder().method(HttpMethod.PATCH).uri(uri).build();
		assertTrue(predicate.test(request));

		predicate = RequestPredicates.DELETE("/p*");
		request = MockServerRequest.builder().method(HttpMethod.DELETE).uri(uri).build();
		assertTrue(predicate.test(request));

		predicate = RequestPredicates.OPTIONS("/p*");
		request = MockServerRequest.builder().method(HttpMethod.OPTIONS).uri(uri).build();
		assertTrue(predicate.test(request));
	}

	@Test
	public void path() {
		URI uri = URI.create("http://localhost/path");
		RequestPredicate predicate = RequestPredicates.path("/p*");
		MockServerRequest request = MockServerRequest.builder().uri(uri).build();
		assertTrue(predicate.test(request));

		request = MockServerRequest.builder().build();
		assertFalse(predicate.test(request));
	}

	@Test
	public void pathEncoded() {
		URI uri = URI.create("http://localhost/foo%20bar");
		RequestPredicate predicate = RequestPredicates.path("/foo bar");
		MockServerRequest request = MockServerRequest.builder().uri(uri).build();
		assertTrue(predicate.test(request));

		request = MockServerRequest.builder().build();
		assertFalse(predicate.test(request));
	}

	@Test
	public void pathPredicates() {
		PathPatternParser parser = new PathPatternParser();
		parser.setCaseSensitive(false);
		Function<String, RequestPredicate> pathPredicates = RequestPredicates.pathPredicates(parser);

		URI uri = URI.create("http://localhost/path");
		RequestPredicate predicate = pathPredicates.apply("/P*");
		MockServerRequest request = MockServerRequest.builder().uri(uri).build();
		assertTrue(predicate.test(request));
	}

	@Test
	public void headers() {
		String name = "MyHeader";
		String value = "MyValue";
		RequestPredicate predicate =
				RequestPredicates.headers(
						headers -> headers.header(name).equals(Collections.singletonList(value)));
		MockServerRequest request = MockServerRequest.builder().header(name, value).build();
		assertTrue(predicate.test(request));

		request = MockServerRequest.builder().build();
		assertFalse(predicate.test(request));
	}

	@Test
	public void contentType() {
		MediaType json = MediaType.APPLICATION_JSON;
		RequestPredicate predicate = RequestPredicates.contentType(json);
		MockServerRequest request = MockServerRequest.builder().header("Content-Type", json.toString()).build();
		assertTrue(predicate.test(request));

		request = MockServerRequest.builder().build();
		assertFalse(predicate.test(request));
	}

	@Test
	public void accept() {
		MediaType json = MediaType.APPLICATION_JSON;
		RequestPredicate predicate = RequestPredicates.accept(json);
		MockServerRequest request = MockServerRequest.builder().header("Accept", json.toString()).build();
		assertTrue(predicate.test(request));

		request = MockServerRequest.builder().header("Accept", MediaType.TEXT_XML_VALUE).build();
		assertFalse(predicate.test(request));
	}

	@Test
	public void pathExtension() {
		RequestPredicate predicate = RequestPredicates.pathExtension("txt");

		URI uri = URI.create("http://localhost/file.txt");
		MockServerRequest request = MockServerRequest.builder().uri(uri).build();
		assertTrue(predicate.test(request));

		uri = URI.create("http://localhost/FILE.TXT");
		request = MockServerRequest.builder().uri(uri).build();
		assertTrue(predicate.test(request));

		predicate = RequestPredicates.pathExtension("bar");
		assertFalse(predicate.test(request));

		uri = URI.create("http://localhost/file.foo");
		request = MockServerRequest.builder().uri(uri).build();
		assertFalse(predicate.test(request));
	}

	@Test
	public void queryParam() {
		MockServerRequest request = MockServerRequest.builder().queryParam("foo", "bar").build();
		RequestPredicate predicate = RequestPredicates.queryParam("foo", s -> s.equals("bar"));
		assertTrue(predicate.test(request));

		predicate = RequestPredicates.queryParam("foo", s -> s.equals("baz"));
		assertFalse(predicate.test(request));
	}

}
