

package org.springframework.test.context.junit.jupiter.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.context.junit.SpringJUnitJupiterTestSuite;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

/**
 * Integration tests which demonstrate how to set up a {@link MockMvc}
 * instance in an {@link BeforeEach @BeforeEach} method with the
 * {@link SpringExtension} (registered via a custom
 * {@link SpringJUnitWebConfig @SpringJUnitWebConfig} composed annotation).
 *
 * To run these tests in an IDE that does not have built-in support for the JUnit
 * Platform, simply run {@link SpringJUnitJupiterTestSuite} as a JUnit 4 test.
 *
 * @author Sam Brannen
 * @since 5.0
 * @see SpringExtension
 * @see SpringJUnitWebConfig
 * @see org.springframework.test.context.junit.jupiter.web.WebSpringExtensionTests
 */
@SpringJUnitWebConfig(WebConfig.class)
class MultipleWebRequestsSpringExtensionTests {

	MockMvc mockMvc;

	@BeforeEach
	void setUpMockMvc(WebApplicationContext wac) {
		this.mockMvc = webAppContextSetup(wac)
			.alwaysExpect(status().isOk())
			.alwaysExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
			.build();
	}

	@Test
	void getPerson42() throws Exception {
		this.mockMvc.perform(get("/person/42").accept(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.name", is("Dilbert")));
	}

	@Test
	void getPerson99() throws Exception {
		this.mockMvc.perform(get("/person/99").accept(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.name", is("Wally")));
	}

}
