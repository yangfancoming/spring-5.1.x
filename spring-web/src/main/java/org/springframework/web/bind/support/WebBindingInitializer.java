

package org.springframework.web.bind.support;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.WebRequest;

/**
 * Callback interface for initializing a {@link WebDataBinder} for performing
 * data binding in the context of a specific web request.
 *

 *
 * @since 2.5
 */
public interface WebBindingInitializer {

	/**
	 * Initialize the given DataBinder.
	 * @param binder the DataBinder to initialize
	 * @since 5.0
	 */
	void initBinder(WebDataBinder binder);

	/**
	 * Initialize the given DataBinder for the given (Servlet) request.
	 * @param binder the DataBinder to initialize
	 * @param request the web request that the data binding happens within
	 * @deprecated as of 5.0 in favor of {@link #initBinder(WebDataBinder)}
	 */
	@Deprecated
	default void initBinder(WebDataBinder binder, WebRequest request) {
		initBinder(binder);
	}

}
