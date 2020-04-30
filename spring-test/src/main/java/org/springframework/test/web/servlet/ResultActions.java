

package org.springframework.test.web.servlet;

/**
 * Allows applying actions, such as expectations, on the result of an executed
 * request.
 *
 * See static factory methods in
 * {@link org.springframework.test.web.servlet.result.MockMvcResultMatchers} and
 * {@link org.springframework.test.web.servlet.result.MockMvcResultHandlers}.
 *
 *
 * @since 3.2
 */
public interface ResultActions {

	/**
	 * Perform an expectation.
	 *
	 * <h4>Example</h4>
	 * <pre class="code">
	 * static imports: MockMvcRequestBuilders.*, MockMvcResultMatchers.*
	 *
	 * mockMvc.perform(get("/person/1"))
	 *   .andExpect(status().isOk())
	 *   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
	 *   .andExpect(jsonPath("$.person.name").value("Jason"));
	 * </pre>
	 *
	 * Or alternatively provide all matchers as a vararg:
	 * <pre class="code">
	 * static imports: MockMvcRequestBuilders.*, MockMvcResultMatchers.*, ResultMatcher.matchAll
	 *
	 * mockMvc.perform(post("/form"))
	 *   .andExpect(matchAll(
	 *       status().isOk(),
	 *       redirectedUrl("/person/1"),
	 *   	 model().size(1),
	 *       model().attributeExists("person"),
	 *       flash().attributeCount(1),
	 *       flash().attribute("message", "success!"))
	 *   );
	 * </pre>
	 */
	ResultActions andExpect(ResultMatcher matcher) throws Exception;

	/**
	 * Perform a general action.
	 *
	 * <h4>Example</h4>
	 * <pre class="code">
	 * static imports: MockMvcRequestBuilders.*, MockMvcResultMatchers.*
	 *
	 * mockMvc.perform(get("/form")).andDo(print());
	 * </pre>
	 */
	ResultActions andDo(ResultHandler handler) throws Exception;

	/**
	 * Return the result of the executed request for direct access to the results.
	 *
	 * @return the result of the request
	 */
	MvcResult andReturn();

}
