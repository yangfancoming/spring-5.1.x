

package org.springframework.web.servlet;

/**
 * Provides additional information about a View such as whether it performs redirects.
 * @since 3.1
 */
public interface SmartView extends View {

	/**
	 * Whether the view performs a redirect.
	 */
	boolean isRedirectView();

}
