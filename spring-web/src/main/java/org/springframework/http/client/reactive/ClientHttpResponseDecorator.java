

package org.springframework.http.client.reactive;

import reactor.core.publisher.Flux;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

/**
 * Wraps another {@link ClientHttpResponse} and delegates all methods to it.
 * Sub-classes can override specific methods selectively.
 *
 * @author Rossen Stoyanchev
 * @since 5.0
 */
public class ClientHttpResponseDecorator implements ClientHttpResponse {

	private final ClientHttpResponse delegate;


	public ClientHttpResponseDecorator(ClientHttpResponse delegate) {
		Assert.notNull(delegate, "Delegate is required");
		this.delegate = delegate;
	}


	public ClientHttpResponse getDelegate() {
		return this.delegate;
	}


	// ServerHttpResponse delegation methods...

	@Override
	public HttpStatus getStatusCode() {
		return this.delegate.getStatusCode();
	}

	@Override
	public int getRawStatusCode() {
		return this.delegate.getRawStatusCode();
	}

	@Override
	public HttpHeaders getHeaders() {
		return this.delegate.getHeaders();
	}

	@Override
	public MultiValueMap<String, ResponseCookie> getCookies() {
		return this.delegate.getCookies();
	}

	@Override
	public Flux<DataBuffer> getBody() {
		return this.delegate.getBody();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [delegate=" + getDelegate() + "]";
	}

}
