

package org.springframework.web.server.handler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import reactor.core.publisher.Mono;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.WebHandler;

/**
 * Default implementation of {@link WebFilterChain}.
 *
 * Each instance of this class represents one link in the chain. The public
 * constructor {@link #DefaultWebFilterChain(WebHandler, List)}
 * initializes the full chain and represents its first link.
 *
 * This class is immutable and thread-safe. It can be created once and
 * re-used to handle request concurrently.
 *
 *
 * @since 5.0
 */
public class DefaultWebFilterChain implements WebFilterChain {

	private final List<WebFilter> allFilters;

	private final WebHandler handler;

	@Nullable
	private final WebFilter currentFilter;

	@Nullable
	private final DefaultWebFilterChain next;


	/**
	 * Public constructor with the list of filters and the target handler to use.
	 * @param handler the target handler
	 * @param filters the filters ahead of the handler
	 * @since 5.1
	 */
	public DefaultWebFilterChain(WebHandler handler, List<WebFilter> filters) {
		Assert.notNull(handler, "WebHandler is required");
		this.allFilters = Collections.unmodifiableList(filters);
		this.handler = handler;
		DefaultWebFilterChain chain = initChain(filters, handler);
		this.currentFilter = chain.currentFilter;
		this.next = chain.next;
	}

	private static DefaultWebFilterChain initChain(List<WebFilter> filters, WebHandler handler) {
		DefaultWebFilterChain chain = new DefaultWebFilterChain(filters, handler, null, null);
		ListIterator<? extends WebFilter> iterator = filters.listIterator(filters.size());
		while (iterator.hasPrevious()) {
			chain = new DefaultWebFilterChain(filters, handler, iterator.previous(), chain);
		}
		return chain;
	}

	/**
	 * Private constructor to represent one link in the chain.
	 */
	private DefaultWebFilterChain(List<WebFilter> allFilters, WebHandler handler,
			@Nullable WebFilter currentFilter, @Nullable DefaultWebFilterChain next) {

		this.allFilters = allFilters;
		this.currentFilter = currentFilter;
		this.handler = handler;
		this.next = next;
	}

	/**
	 * Public constructor with the list of filters and the target handler to use.
	 * @param handler the target handler
	 * @param filters the filters ahead of the handler
	 * @deprecated as of 5.1 this constructor is deprecated in favor of
	 * {@link #DefaultWebFilterChain(WebHandler, List)}.
	 */
	@Deprecated
	public DefaultWebFilterChain(WebHandler handler, WebFilter... filters) {
		this(handler, Arrays.asList(filters));
	}


	public List<WebFilter> getFilters() {
		return this.allFilters;
	}

	public WebHandler getHandler() {
		return this.handler;
	}


	@Override
	public Mono<Void> filter(ServerWebExchange exchange) {
		return Mono.defer(() ->
				this.currentFilter != null && this.next != null ?
						this.currentFilter.filter(exchange, this.next) :
						this.handler.handle(exchange));
	}

}
