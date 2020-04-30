

package org.springframework.web.reactive.result.method.annotation;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.AbstractHttpHandlerIntegrationTests;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;

import static org.springframework.http.RequestEntity.*;

/**
 * Base class for integration tests with {@code @RequestMapping methods}.
 *
 *
 */
public abstract class AbstractRequestMappingIntegrationTests extends AbstractHttpHandlerIntegrationTests {

	private RestTemplate restTemplate = new RestTemplate();

	private ApplicationContext applicationContext;


	@Override
	protected HttpHandler createHttpHandler() {
		this.restTemplate = initRestTemplate();
		this.applicationContext = initApplicationContext();
		return WebHttpHandlerBuilder.applicationContext(this.applicationContext).build();
	}

	protected abstract ApplicationContext initApplicationContext();

	protected RestTemplate initRestTemplate() {
		return new RestTemplate();
	}

	protected ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}

	protected RestTemplate getRestTemplate() {
		return this.restTemplate;
	}


	<T> ResponseEntity<T> performGet(String url, MediaType out, Class<T> type) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(out));
		return getRestTemplate().exchange(prepareGet(url, headers), type);
	}

	<T> ResponseEntity<T> performGet(String url, HttpHeaders headers, Class<T> type) throws Exception {
		return getRestTemplate().exchange(prepareGet(url, headers), type);
	}

	<T> ResponseEntity<T> performGet(String url, MediaType out, ParameterizedTypeReference<T> type)
			throws Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(out));
		return this.restTemplate.exchange(prepareGet(url, headers), type);
	}

	<T> ResponseEntity<T> performOptions(String url, HttpHeaders headers, Class<T> type)
			throws Exception {

		return getRestTemplate().exchange(prepareOptions(url, headers), type);
	}

	<T> ResponseEntity<T> performPost(String url, MediaType in, Object body, MediaType out, Class<T> type)
			throws Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(in);
		if (out != null) {
			headers.setAccept(Collections.singletonList(out));
		}
		return  getRestTemplate().exchange(preparePost(url, headers, body), type);
	}

	<T> ResponseEntity<T> performPost(String url, HttpHeaders headers, Object body,
			Class<T> type) throws Exception {

		return  getRestTemplate().exchange(preparePost(url, headers, body), type);
	}

	<T> ResponseEntity<T> performPost(String url, MediaType in, Object body, MediaType out,
			ParameterizedTypeReference<T> type) throws Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(in);
		if (out != null) {
			headers.setAccept(Collections.singletonList(out));
		}
		return getRestTemplate().exchange(preparePost(url, headers, body), type);
	}

	private RequestEntity<Void> prepareGet(String url, HttpHeaders headers) throws Exception {
		URI uri = new URI("http://localhost:" + this.port + url);
		RequestEntity.HeadersBuilder<?> builder = get(uri);
		addHeaders(builder, headers);
		return builder.build();
	}

	private RequestEntity<Void> prepareOptions(String url, HttpHeaders headers) throws Exception {
		URI uri = new URI("http://localhost:" + this.port + url);
		RequestEntity.HeadersBuilder<?> builder = options(uri);
		addHeaders(builder, headers);
		return builder.build();
	}

	private void addHeaders(RequestEntity.HeadersBuilder<?> builder, HttpHeaders headers) {
		for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
			for (String value : entry.getValue()) {
				builder.header(entry.getKey(), value);
			}
		}
	}

	private RequestEntity<?> preparePost(String url, HttpHeaders headers, Object body) throws Exception {
		URI uri = new URI("http://localhost:" + this.port + url);
		RequestEntity.BodyBuilder builder = post(uri);
		addHeaders(builder, headers);
		return builder.body(body);
	}

}
