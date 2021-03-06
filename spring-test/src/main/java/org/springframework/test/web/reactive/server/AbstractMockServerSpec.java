

package org.springframework.test.web.reactive.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import org.springframework.web.server.session.DefaultWebSessionManager;
import org.springframework.web.server.session.WebSessionManager;

/**
 * Base class for implementations of {@link WebTestClient.MockServerSpec}.
 *
 *
 * @since 5.0
 * @param <B> a self reference to the builder type
 */
abstract class AbstractMockServerSpec<B extends WebTestClient.MockServerSpec<B>>
		implements WebTestClient.MockServerSpec<B> {

	@Nullable
	private List<WebFilter> filters;

	@Nullable
	private WebSessionManager sessionManager;

	@Nullable
	private List<MockServerConfigurer> configurers;


	AbstractMockServerSpec() {
		// Default instance to be re-used across requests, unless one is configured explicitly
		this.sessionManager = new DefaultWebSessionManager();
	}


	@Override
	public <T extends B> T webFilter(WebFilter... filters) {
		if (filters.length > 0) {
			this.filters = (this.filters != null ? this.filters : new ArrayList<>(4));
			this.filters.addAll(Arrays.asList(filters));
		}
		return self();
	}

	@Override
	public <T extends B> T webSessionManager(WebSessionManager sessionManager) {
		this.sessionManager = sessionManager;
		return self();
	}

	@Override
	public <T extends B> T apply(MockServerConfigurer configurer) {
		configurer.afterConfigureAdded(this);
		this.configurers = (this.configurers != null ? this.configurers : new ArrayList<>(4));
		this.configurers.add(configurer);
		return self();
	}

	@SuppressWarnings("unchecked")
	private <T extends B> T self() {
		return (T) this;
	}

	@Override
	public WebTestClient.Builder configureClient() {
		WebHttpHandlerBuilder builder = initHttpHandlerBuilder();
		if (!CollectionUtils.isEmpty(this.filters)) {
			builder.filters(theFilters -> theFilters.addAll(0, this.filters));
		}
		if (!builder.hasSessionManager() && this.sessionManager != null) {
			builder.sessionManager(this.sessionManager);
		}
		if (!CollectionUtils.isEmpty(this.configurers)) {
			this.configurers.forEach(configurer -> configurer.beforeServerCreated(builder));
		}
		return new DefaultWebTestClientBuilder(builder);
	}

	/**
	 * Sub-classes must create an {@code WebHttpHandlerBuilder} that will then
	 * be used to create the HttpHandler for the mock server.
	 */
	protected abstract WebHttpHandlerBuilder initHttpHandlerBuilder();

	@Override
	public WebTestClient build() {
		return configureClient().build();
	}

}
