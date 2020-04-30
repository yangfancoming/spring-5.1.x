

package org.springframework.mock.http.client;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

/**
 * An extension of {@link MockClientHttpRequest} that also implements
 * {@link org.springframework.http.client.AsyncClientHttpRequest} by wrapping the response in a
 * {@link SettableListenableFuture}.
 *
 *
 * @author Sam Brannen
 * @since 4.1
 * @deprecated as of Spring 5.0, with no direct replacement
 */
@Deprecated
public class MockAsyncClientHttpRequest extends MockClientHttpRequest implements org.springframework.http.client.AsyncClientHttpRequest {

	public MockAsyncClientHttpRequest() {
	}

	public MockAsyncClientHttpRequest(HttpMethod httpMethod, URI uri) {
		super(httpMethod, uri);
	}


	@Override
	public ListenableFuture<ClientHttpResponse> executeAsync() throws IOException {
		SettableListenableFuture<ClientHttpResponse> future = new SettableListenableFuture<>();
		future.set(execute());
		return future;
	}

}
