

package org.springframework.test.web.servlet;

/**
 * A {@code ResultMatcher} matches the result of an executed request against
 * some expectation.
 *
 * See static factory methods in
 * {@link org.springframework.test.web.servlet.result.MockMvcResultMatchers
 * MockMvcResultMatchers}.
 *
 * <h3>Example Using Status and Content Result Matchers</h3>
 *
 * <pre class="code">
 * import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
 * import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
 * import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
 *
 * // ...
 *
 * WebApplicationContext wac = ...;
 *
 * MockMvc mockMvc = webAppContextSetup(wac).build();
 *
 * mockMvc.perform(get("/form"))
 *   .andExpect(status().isOk())
 *   .andExpect(content().mimeType(MediaType.APPLICATION_JSON));
 * </pre>
 *
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 * @since 3.2
 */
@FunctionalInterface
public interface ResultMatcher {

	/**
	 * Assert the result of an executed request.
	 * @param result the result of the executed request
	 * @throws Exception if a failure occurs
	 */
	void match(MvcResult result) throws Exception;


	/**
	 * Static method for matching with an array of result matchers.
	 * @param matchers the matchers
	 * @since 5.1
	 */
	static ResultMatcher matchAll(ResultMatcher... matchers) {
		return result -> {
			for (ResultMatcher matcher : matchers) {
			matcher.match(result);
			}
		};
	}

}
