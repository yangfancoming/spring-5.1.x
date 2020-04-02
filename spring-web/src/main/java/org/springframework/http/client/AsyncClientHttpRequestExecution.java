

package org.springframework.http.client;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * Represents the context of a client-side HTTP request execution.
 *
 * Used to invoke the next interceptor in the interceptor chain, or -
 * if the calling interceptor is last - execute the request itself.
 *
 * @author Jakub Narloch
 * @author Rossen Stoyanchev
 * @since 4.3
 * @see AsyncClientHttpRequestInterceptor
 * @deprecated as of Spring 5.0, in favor of
 * {@link org.springframework.web.reactive.function.client.ExchangeFilterFunction}
 */
@Deprecated
public interface AsyncClientHttpRequestExecution {

	/**
	 * Resume the request execution by invoking the next interceptor in the chain
	 * or executing the request to the remote service.
	 * @param request the HTTP request, containing the HTTP method and headers
	 * @param body the body of the request
	 * @return a corresponding future handle
	 * @throws IOException in case of I/O errors
	 */
	ListenableFuture<ClientHttpResponse> executeAsync(HttpRequest request, byte[] body) throws IOException;

}
