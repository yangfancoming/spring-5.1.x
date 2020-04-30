
package org.springframework.http.server;

import java.net.URI;

import org.springframework.lang.Nullable;

/**
 * Represents the complete path for a request.
 *
 *
 * @since 5.0
 */
public interface RequestPath extends PathContainer {

	/**
	 * Returns the portion of the URL path that represents the application.
	 * The context path is always at the beginning of the path and starts but
	 * does not end with "/". It is shared for URLs of the same application.
	 * The context path may come from the underlying runtime API such as
	 * when deploying as a WAR to a Servlet container or it may be assigned in
	 * a WebFlux application through the use of
	 * {@link org.springframework.http.server.reactive.ContextPathCompositeHandler
	 * ContextPathCompositeHandler}.
	 */
	PathContainer contextPath();

	/**
	 * The portion of the request path after the context path.
	 */
	PathContainer pathWithinApplication();

	/**
	 * Return a new {@code RequestPath} instance with a modified context path.
	 * The new context path must match 0 or more path segments at the start.
	 * @param contextPath the new context path
	 * @return a new {@code RequestPath} instance
	 */
	RequestPath modifyContextPath(String contextPath);


	/**
	 * Create a new {@code RequestPath} with the given parameters.
	 */
	static RequestPath parse(URI uri, @Nullable String contextPath) {
		return new DefaultRequestPath(uri, contextPath);
	}

}
