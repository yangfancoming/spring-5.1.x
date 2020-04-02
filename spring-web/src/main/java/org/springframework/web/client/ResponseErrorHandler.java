

package org.springframework.web.client;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Strategy interface used by the {@link RestTemplate} to determine
 * whether a particular response has an error or not.
 *
 * @author Arjen Poutsma
 * @since 3.0
 */
public interface ResponseErrorHandler {

	/**
	 * Indicate whether the given response has any errors.
	 * Implementations will typically inspect the
	 * {@link ClientHttpResponse#getStatusCode() HttpStatus} of the response.
	 * @param response the response to inspect
	 * @return {@code true} if the response indicates an error; {@code false} otherwise
	 * @throws IOException in case of I/O errors
	 */
	boolean hasError(ClientHttpResponse response) throws IOException;

	/**
	 * Handle the error in the given response.
	 * This method is only called when {@link #hasError(ClientHttpResponse)}
	 * has returned {@code true}.
	 * @param response the response with the error
	 * @throws IOException in case of I/O errors
	 */
	void handleError(ClientHttpResponse response) throws IOException;

	/**
	 * Alternative to {@link #handleError(ClientHttpResponse)} with extra
	 * information providing access to the request URL and HTTP method.
	 * @param url the request URL
	 * @param method the HTTP method
	 * @param response the response with the error
	 * @throws IOException in case of I/O errors
	 * @since 5.0
	 */
	default void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
		handleError(response);
	}

}
