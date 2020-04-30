

package org.springframework.test.web.servlet;

import javax.servlet.ServletContext;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Builds a {@link MockHttpServletRequest}.
 *
 * See static factory methods in
 * {@link org.springframework.test.web.servlet.request.MockMvcRequestBuilders MockMvcRequestBuilders}.
 *
 * @author Arjen Poutsma
 *
 * @since 3.2
 */
public interface RequestBuilder {

	/**
	 * Build the request.
	 * @param servletContext the {@link ServletContext} to use to create the request
	 * @return the request
	 */
	MockHttpServletRequest buildRequest(ServletContext servletContext);

}
