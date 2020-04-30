

package org.springframework.test.web.servlet.samples.standalone.resultmatchers;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

/**
 * Examples of expectations on flash attributes.
 *
 *
 */
public class FlashAttributeAssertionTests {

	private MockMvc mockMvc;


	@Before
	public void setup() {
		this.mockMvc = standaloneSetup(new PersonController())
				.alwaysExpect(status().isFound())
				.alwaysExpect(flash().attributeCount(3))
				.build();
	}

	@Test
	public void testExists() throws Exception {
		this.mockMvc.perform(post("/persons"))
			.andExpect(flash().attributeExists("one", "two", "three"));
	}

	@Test
	public void testEqualTo() throws Exception {
		this.mockMvc.perform(post("/persons"))
			.andExpect(flash().attribute("one", "1"))
			.andExpect(flash().attribute("two", 2.222))
			.andExpect(flash().attribute("three", new URL("https://example.com")))
			.andExpect(flash().attribute("one", equalTo("1")))	// Hamcrest...
			.andExpect(flash().attribute("two", equalTo(2.222)))
			.andExpect(flash().attribute("three", equalTo(new URL("https://example.com"))));
	}

	@Test
	public void testMatchers() throws Exception {
		this.mockMvc.perform(post("/persons"))
			.andExpect(flash().attribute("one", containsString("1")))
			.andExpect(flash().attribute("two", closeTo(2, 0.5)))
			.andExpect(flash().attribute("three", notNullValue()));
	}


	@Controller
	private static class PersonController {

		@RequestMapping(value="/persons", method=RequestMethod.POST)
		public String save(RedirectAttributes redirectAttrs) throws Exception {
			redirectAttrs.addFlashAttribute("one", "1");
			redirectAttrs.addFlashAttribute("two", 2.222);
			redirectAttrs.addFlashAttribute("three", new URL("https://example.com"));
			return "redirect:/person/1";
		}
	}
}
