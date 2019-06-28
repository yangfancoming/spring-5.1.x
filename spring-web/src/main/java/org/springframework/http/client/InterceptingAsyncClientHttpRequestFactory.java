

package org.springframework.http.client;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;

/**
 * Wrapper for a {@link AsyncClientHttpRequestFactory} that has support for
 * {@link AsyncClientHttpRequestInterceptor AsyncClientHttpRequestInterceptors}.
 *
 * @author Jakub Narloch
 * @since 4.3
 * @see InterceptingAsyncClientHttpRequest
 * @deprecated as of Spring 5.0, with no direct replacement
 */
@Deprecated
public class InterceptingAsyncClientHttpRequestFactory implements AsyncClientHttpRequestFactory {

	private AsyncClientHttpRequestFactory delegate;

	private List<AsyncClientHttpRequestInterceptor> interceptors;


	/**
	 * Create new instance of {@link InterceptingAsyncClientHttpRequestFactory}
	 * with delegated request factory and list of interceptors.
	 * @param delegate the request factory to delegate to
	 * @param interceptors the list of interceptors to use
	 */
	public InterceptingAsyncClientHttpRequestFactory(AsyncClientHttpRequestFactory delegate,
			@Nullable List<AsyncClientHttpRequestInterceptor> interceptors) {

		this.delegate = delegate;
		this.interceptors = (interceptors != null ? interceptors : Collections.emptyList());
	}


	@Override
	public AsyncClientHttpRequest createAsyncRequest(URI uri, HttpMethod method) {
		return new InterceptingAsyncClientHttpRequest(this.delegate, this.interceptors, uri, method);
	}

}
