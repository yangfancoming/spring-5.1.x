

package org.springframework.web.reactive.function.client.support;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.ClientResponse;

import static java.util.Collections.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class ClientResponseWrapperTests {

	private ClientResponse mockResponse;

	private ClientResponseWrapper wrapper;

	@Before
	public void createWrapper() {
		this.mockResponse = mock(ClientResponse.class);
		this.wrapper = new ClientResponseWrapper(mockResponse);
	}

	@Test
	public void response() {
		assertSame(mockResponse, wrapper.response());
	}

	@Test
	public void statusCode() {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		when(mockResponse.statusCode()).thenReturn(status);

		assertSame(status, wrapper.statusCode());
	}

	@Test
	public void rawStatusCode() {
		int status = 999;
		when(mockResponse.rawStatusCode()).thenReturn(status);

		assertEquals(status, wrapper.rawStatusCode());
	}

	@Test
	public void headers() {
		ClientResponse.Headers headers = mock(ClientResponse.Headers.class);
		when(mockResponse.headers()).thenReturn(headers);

		assertSame(headers, wrapper.headers());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void cookies() {
		MultiValueMap<String, ResponseCookie> cookies = mock(MultiValueMap.class);
		when(mockResponse.cookies()).thenReturn(cookies);

		assertSame(cookies, wrapper.cookies());
	}

	@Test
	public void bodyExtractor() {
		Mono<String> result = Mono.just("foo");
		BodyExtractor<Mono<String>, ReactiveHttpInputMessage> extractor = BodyExtractors.toMono(String.class);
		when(mockResponse.body(extractor)).thenReturn(result);

		assertSame(result, wrapper.body(extractor));
	}

	@Test
	public void bodyToMonoClass() {
		Mono<String> result = Mono.just("foo");
		when(mockResponse.bodyToMono(String.class)).thenReturn(result);

		assertSame(result, wrapper.bodyToMono(String.class));
	}

	@Test
	public void bodyToMonoParameterizedTypeReference() {
		Mono<String> result = Mono.just("foo");
		ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {};
		when(mockResponse.bodyToMono(reference)).thenReturn(result);

		assertSame(result, wrapper.bodyToMono(reference));
	}

	@Test
	public void bodyToFluxClass() {
		Flux<String> result = Flux.just("foo");
		when(mockResponse.bodyToFlux(String.class)).thenReturn(result);

		assertSame(result, wrapper.bodyToFlux(String.class));
	}

	@Test
	public void bodyToFluxParameterizedTypeReference() {
		Flux<String> result = Flux.just("foo");
		ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {};
		when(mockResponse.bodyToFlux(reference)).thenReturn(result);

		assertSame(result, wrapper.bodyToFlux(reference));
	}

	@Test
	public void toEntityClass() {
		Mono<ResponseEntity<String>> result = Mono.just(new ResponseEntity<>("foo", HttpStatus.OK));
		when(mockResponse.toEntity(String.class)).thenReturn(result);

		assertSame(result, wrapper.toEntity(String.class));
	}

	@Test
	public void toEntityParameterizedTypeReference() {
		Mono<ResponseEntity<String>> result = Mono.just(new ResponseEntity<>("foo", HttpStatus.OK));
		ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {};
		when(mockResponse.toEntity(reference)).thenReturn(result);

		assertSame(result, wrapper.toEntity(reference));
	}

	@Test
	public void toEntityListClass() {
		Mono<ResponseEntity<List<String>>> result = Mono.just(new ResponseEntity<>(singletonList("foo"), HttpStatus.OK));
		when(mockResponse.toEntityList(String.class)).thenReturn(result);

		assertSame(result, wrapper.toEntityList(String.class));
	}

	@Test
	public void toEntityListParameterizedTypeReference() {
		Mono<ResponseEntity<List<String>>> result = Mono.just(new ResponseEntity<>(singletonList("foo"), HttpStatus.OK));
		ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {};
		when(mockResponse.toEntityList(reference)).thenReturn(result);

		assertSame(result, wrapper.toEntityList(reference));
	}



}
