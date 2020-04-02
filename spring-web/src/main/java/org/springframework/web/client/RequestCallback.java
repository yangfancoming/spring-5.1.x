

package org.springframework.web.client;

import java.io.IOException;
import java.lang.reflect.Type;

import org.springframework.http.client.ClientHttpRequest;

/**
 * Callback interface for code that operates on a {@link ClientHttpRequest}.
 * Allows manipulating the request headers, and write to the request body.
 *
 * Used internally by the {@link RestTemplate}, but also useful for
 * application code. There several available factory methods:
 * <ul>
 * <li>{@link RestTemplate#acceptHeaderRequestCallback(Class)}
 * <li>{@link RestTemplate#httpEntityCallback(Object)}
 * <li>{@link RestTemplate#httpEntityCallback(Object, Type)}
 * </ul>
 *
 * @author Arjen Poutsma
 * @see RestTemplate#execute
 * @since 3.0
 */
@FunctionalInterface
public interface RequestCallback {

	/**
	 * Gets called by {@link RestTemplate#execute} with an opened {@code ClientHttpRequest}.
	 * Does not need to care about closing the request or about handling errors:
	 * this will all be handled by the {@code RestTemplate}.
	 * @param request the active HTTP request
	 * @throws IOException in case of I/O errors
	 */
	void doWithRequest(ClientHttpRequest request) throws IOException;

}
