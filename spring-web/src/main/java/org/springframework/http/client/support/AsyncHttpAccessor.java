

package org.springframework.http.client.support;

import java.io.IOException;
import java.net.URI;

import org.apache.commons.logging.Log;

import org.springframework.http.HttpLogging;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Base class for {@link org.springframework.web.client.AsyncRestTemplate}
 * and other HTTP accessing gateway helpers, defining common properties
 * such as the {@link org.springframework.http.client.AsyncClientHttpRequestFactory}
 * to operate on.
 *
 * <p>Not intended to be used directly. See
 * {@link org.springframework.web.client.AsyncRestTemplate}.
 *
 * @author Arjen Poutsma
 * @since 4.0
 * @see org.springframework.web.client.AsyncRestTemplate
 * @deprecated as of Spring 5.0, with no direct replacement
 */
@Deprecated
public class AsyncHttpAccessor {

	/** Logger available to subclasses. */
	protected final Log logger = HttpLogging.forLogName(getClass());

	@Nullable
	private org.springframework.http.client.AsyncClientHttpRequestFactory asyncRequestFactory;


	/**
	 * Set the request factory that this accessor uses for obtaining {@link
	 * org.springframework.http.client.ClientHttpRequest HttpRequests}.
	 */
	public void setAsyncRequestFactory(
			org.springframework.http.client.AsyncClientHttpRequestFactory asyncRequestFactory) {

		Assert.notNull(asyncRequestFactory, "AsyncClientHttpRequestFactory must not be null");
		this.asyncRequestFactory = asyncRequestFactory;
	}

	/**
	 * Return the request factory that this accessor uses for obtaining {@link
	 * org.springframework.http.client.ClientHttpRequest HttpRequests}.
	 */
	public org.springframework.http.client.AsyncClientHttpRequestFactory getAsyncRequestFactory() {
		Assert.state(this.asyncRequestFactory != null, "No AsyncClientHttpRequestFactory set");
		return this.asyncRequestFactory;
	}

	/**
	 * Create a new {@link org.springframework.http.client.AsyncClientHttpRequest} via this template's
	 * {@link org.springframework.http.client.AsyncClientHttpRequestFactory}.
	 * @param url the URL to connect to
	 * @param method the HTTP method to execute (GET, POST, etc.)
	 * @return the created request
	 * @throws IOException in case of I/O errors
	 */
	protected org.springframework.http.client.AsyncClientHttpRequest createAsyncRequest(URI url, HttpMethod method)
			throws IOException {

		org.springframework.http.client.AsyncClientHttpRequest request =
				getAsyncRequestFactory().createAsyncRequest(url, method);
		if (logger.isDebugEnabled()) {
			logger.debug("Created asynchronous " + method.name() + " request for \"" + url + "\"");
		}
		return request;
	}

}
