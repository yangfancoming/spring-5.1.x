

package org.springframework.http.client;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpMethod;

/**
 * Factory for {@link AsyncClientHttpRequest} objects.
 * Requests are created by the {@link #createAsyncRequest(URI, HttpMethod)} method.
 *
 * @author Arjen Poutsma
 * @since 4.0
 * @deprecated as of Spring 5.0, in favor of {@link org.springframework.http.client.reactive.ClientHttpConnector}
 */
@Deprecated
public interface AsyncClientHttpRequestFactory {

	/**
	 * Create a new asynchronous {@link AsyncClientHttpRequest} for the specified URI
	 * and HTTP method.
	 * The returned request can be written to, and then executed by calling
	 * {@link AsyncClientHttpRequest#executeAsync()}.
	 * @param uri the URI to create a request for
	 * @param httpMethod the HTTP method to execute
	 * @return the created request
	 * @throws IOException in case of I/O errors
	 */
	AsyncClientHttpRequest createAsyncRequest(URI uri, HttpMethod httpMethod) throws IOException;

}
