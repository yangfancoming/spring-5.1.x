

package org.springframework.http.client.support;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

/**
 * Base class for {@link org.springframework.web.client.RestTemplate}
 * and other HTTP accessing gateway helpers, adding interceptor-related
 * properties to {@link HttpAccessor}'s common properties.
 *
 * <p>Not intended to be used directly.
 * See {@link org.springframework.web.client.RestTemplate} for an entry point.
 *
 * @author Arjen Poutsma

 * @since 3.0
 * @see ClientHttpRequestInterceptor
 * @see InterceptingClientHttpRequestFactory
 * @see org.springframework.web.client.RestTemplate
 */
public abstract class InterceptingHttpAccessor extends HttpAccessor {

	private final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();

	@Nullable
	private volatile ClientHttpRequestFactory interceptingRequestFactory;


	/**
	 * Set the request interceptors that this accessor should use.
	 * <p>The interceptors will get sorted according to their order
	 * once the {@link ClientHttpRequestFactory} will be built.
	 * @see #getRequestFactory()
	 * @see AnnotationAwareOrderComparator
	 */
	public void setInterceptors(List<ClientHttpRequestInterceptor> interceptors) {
		// Take getInterceptors() List as-is when passed in here
		if (this.interceptors != interceptors) {
			this.interceptors.clear();
			this.interceptors.addAll(interceptors);
			AnnotationAwareOrderComparator.sort(this.interceptors);
		}
	}

	/**
	 * Return the request interceptors that this accessor uses.
	 * <p>The returned {@link List} is active and may get appended to.
	 */
	public List<ClientHttpRequestInterceptor> getInterceptors() {
		return this.interceptors;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setRequestFactory(ClientHttpRequestFactory requestFactory) {
		super.setRequestFactory(requestFactory);
		this.interceptingRequestFactory = null;
	}

	/**
	 * Overridden to expose an {@link InterceptingClientHttpRequestFactory}
	 * if necessary.
	 * @see #getInterceptors()
	 */
	@Override
	public ClientHttpRequestFactory getRequestFactory() {
		List<ClientHttpRequestInterceptor> interceptors = getInterceptors();
		if (!CollectionUtils.isEmpty(interceptors)) {
			ClientHttpRequestFactory factory = this.interceptingRequestFactory;
			if (factory == null) {
				factory = new InterceptingClientHttpRequestFactory(super.getRequestFactory(), interceptors);
				this.interceptingRequestFactory = factory;
			}
			return factory;
		}
		else {
			return super.getRequestFactory();
		}
	}

}
