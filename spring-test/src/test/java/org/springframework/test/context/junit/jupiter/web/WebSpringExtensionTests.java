

package org.springframework.test.context.junit.jupiter.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.context.junit.SpringJUnitJupiterTestSuite;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

/**
 * Integration tests which demonstrate use of the Spring MVC Test Framework and
 * the Spring TestContext Framework with JUnit Jupiter and the
 * {@link SpringExtension} (via a custom
 * {@link SpringJUnitWebConfig @SpringJUnitWebConfig} composed annotation).
 *
 * <p>Note how the {@link #springMvcTest(WebApplicationContext)} test method
 * has the {@link WebApplicationContext} injected as a method parameter.
 * This allows the {@link MockMvc} instance to be configured local to the
 * test method without any fields in the test class.
 *
 * <p>To run these tests in an IDE that does not have built-in support for the JUnit
 * Platform, simply run {@link SpringJUnitJupiterTestSuite} as a JUnit 4 test.
 *
 * @author Sam Brannen
 * @since 5.0
 * @see SpringExtension
 * @see SpringJUnitWebConfig
 * @see org.springframework.test.context.junit.jupiter.web.MultipleWebRequestsSpringExtensionTests
 * @see org.springframework.test.context.junit.jupiter.SpringExtensionTests
 * @see org.springframework.test.context.junit.jupiter.ComposedSpringExtensionTests
 */
@SpringJUnitWebConfig(WebConfig.class)
@DisplayName("Web SpringExtension Tests")
class WebSpringExtensionTests {

	@Test
	void springMvcTest(WebApplicationContext wac) throws Exception {
		webAppContextSetup(wac).build()
			.perform(get("/person/42").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name", is("Dilbert")));
	}

}
