

package org.springframework.test.web.servlet.samples.standalone.resultmatchers;

import org.junit.Before;
import org.junit.Test;

import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

/**
 * Examples of expectations on forwarded or redirected URLs.
 *
 * @author Rossen Stoyanchev
 */
public class UrlAssertionTests {

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = standaloneSetup(new SimpleController()).build();
	}

	@Test
	public void testRedirect() throws Exception {
		this.mockMvc.perform(get("/persons")).andExpect(redirectedUrl("/persons/1"));
	}

	@Test
	public void testRedirectPattern() throws Exception {
		this.mockMvc.perform(get("/persons")).andExpect(redirectedUrlPattern("/persons/*"));
	}

	@Test
	public void testForward() throws Exception {
		this.mockMvc.perform(get("/")).andExpect(forwardedUrl("/home"));
	}

	@Test
	public void testForwardPattern() throws Exception {
		this.mockMvc.perform(get("/")).andExpect(forwardedUrlPattern("/ho?e"));
	}

	@Controller
	private static class SimpleController {

		@RequestMapping("/persons")
		public String save() {
			return "redirect:/persons/1";
		}

		@RequestMapping("/")
		public String forward() {
			return "forward:/home";
		}
	}
}
