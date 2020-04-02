

package org.springframework.test.web.servlet;

/**
 * Builds a {@link MockMvc} instance.
 *
 * See static factory methods in
 * {@link org.springframework.test.web.servlet.setup.MockMvcBuilders MockMvcBuilders}.
 *
 * @author Rossen Stoyanchev
 * @since 3.2
 */
public interface MockMvcBuilder {

	/**
	 * Build a {@link MockMvc} instance.
	 */
	MockMvc build();

}
