

package org.springframework.http.client.support;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

/**
 * The HTTP accessor that extends the base {@link AsyncHttpAccessor} with
 * request intercepting functionality.
 *
 * @author Jakub Narloch
 * @author Rossen Stoyanchev
 * @since 4.3
 * @deprecated as of Spring 5.0, with no direct replacement
 */
@Deprecated
public abstract class InterceptingAsyncHttpAccessor extends AsyncHttpAccessor {

	private List<org.springframework.http.client.AsyncClientHttpRequestInterceptor> interceptors =
			new ArrayList<>();


	/**
	 * Set the request interceptors that this accessor should use.
	 * @param interceptors the list of interceptors
	 */
	public void setInterceptors(List<org.springframework.http.client.AsyncClientHttpRequestInterceptor> interceptors) {
		this.interceptors = interceptors;
	}

	/**
	 * Return the request interceptor that this accessor uses.
	 */
	public List<org.springframework.http.client.AsyncClientHttpRequestInterceptor> getInterceptors() {
		return this.interceptors;
	}


	@Override
	public org.springframework.http.client.AsyncClientHttpRequestFactory getAsyncRequestFactory() {
		org.springframework.http.client.AsyncClientHttpRequestFactory delegate = super.getAsyncRequestFactory();
		if (!CollectionUtils.isEmpty(getInterceptors())) {
			return new org.springframework.http.client.InterceptingAsyncClientHttpRequestFactory(delegate, getInterceptors());
		}
		else {
			return delegate;
		}
	}

}
