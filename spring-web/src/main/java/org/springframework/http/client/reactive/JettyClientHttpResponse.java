

package org.springframework.http.client.reactive;

import java.net.HttpCookie;
import java.util.List;

import org.eclipse.jetty.reactive.client.ReactiveResponse;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * {@link ClientHttpResponse} implementation for the Jetty ReactiveStreams HTTP client.
 *
 * @author Sebastien Deleuze
 * @since 5.1
 * @see <a href="https://github.com/jetty-project/jetty-reactive-httpclient">Jetty ReactiveStreams HttpClient</a>
 */
class JettyClientHttpResponse implements ClientHttpResponse {

	private final ReactiveResponse reactiveResponse;

	private final Flux<DataBuffer> content;


	public JettyClientHttpResponse(ReactiveResponse reactiveResponse, Publisher<DataBuffer> content) {
		this.reactiveResponse = reactiveResponse;
		this.content = Flux.from(content);
	}


	@Override
	public HttpStatus getStatusCode() {
		return HttpStatus.valueOf(getRawStatusCode());
	}

	@Override
	public int getRawStatusCode() {
		return this.reactiveResponse.getStatus();
	}

	@Override
	public MultiValueMap<String, ResponseCookie> getCookies() {
		MultiValueMap<String, ResponseCookie> result = new LinkedMultiValueMap<>();
		List<String> cookieHeader = getHeaders().get(HttpHeaders.SET_COOKIE);
		if (cookieHeader != null) {
			cookieHeader.forEach(header ->
				HttpCookie.parse(header)
						.forEach(cookie -> result.add(cookie.getName(),
								ResponseCookie.from(cookie.getName(), cookie.getValue())
						.domain(cookie.getDomain())
						.path(cookie.getPath())
						.maxAge(cookie.getMaxAge())
						.secure(cookie.getSecure())
						.httpOnly(cookie.isHttpOnly())
						.build())));
		}
		return CollectionUtils.unmodifiableMultiValueMap(result);
	}

	@Override
	public Flux<DataBuffer> getBody() {
		return this.content;
	}

	@Override
	public HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		this.reactiveResponse.getHeaders().stream()
				.forEach(field -> headers.add(field.getName(), field.getValue()));
		return headers;
	}

}
