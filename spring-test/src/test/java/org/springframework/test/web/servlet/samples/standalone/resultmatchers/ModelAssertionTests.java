

package org.springframework.test.web.servlet.samples.standalone.resultmatchers;

import javax.validation.Valid;

import org.junit.Before;
import org.junit.Test;

import org.springframework.stereotype.Controller;
import org.springframework.test.web.Person;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

/**
 * Examples of expectations on the content of the model prepared by the controller.
 *
 *
 */
public class ModelAssertionTests {

	private MockMvc mockMvc;


	@Before
	public void setup() {

		SampleController controller = new SampleController("a string value", 3, new Person("a name"));

		this.mockMvc = standaloneSetup(controller)
				.defaultRequest(get("/"))
				.alwaysExpect(status().isOk())
				.setControllerAdvice(new ModelAttributeAdvice())
				.build();
	}

	@Test
	public void testAttributeEqualTo() throws Exception {
		mockMvc.perform(get("/"))
			.andExpect(model().attribute("integer", 3))
			.andExpect(model().attribute("string", "a string value"))
			.andExpect(model().attribute("integer", equalTo(3))) // Hamcrest...
			.andExpect(model().attribute("string", equalTo("a string value")))
			.andExpect(model().attribute("globalAttrName", equalTo("Global Attribute Value")));
	}

	@Test
	public void testAttributeExists() throws Exception {
		mockMvc.perform(get("/"))
			.andExpect(model().attributeExists("integer", "string", "person"))
			.andExpect(model().attribute("integer", notNullValue()))  // Hamcrest...
			.andExpect(model().attribute("INTEGER", nullValue()));
	}

	@Test
	public void testAttributeHamcrestMatchers() throws Exception {
		mockMvc.perform(get("/"))
			.andExpect(model().attribute("integer", equalTo(3)))
			.andExpect(model().attribute("string", allOf(startsWith("a string"), endsWith("value"))))
			.andExpect(model().attribute("person", hasProperty("name", equalTo("a name"))));
	}

	@Test
	public void testHasErrors() throws Exception {
		mockMvc.perform(post("/persons")).andExpect(model().attributeHasErrors("person"));
	}

	@Test
	public void testHasNoErrors() throws Exception {
		mockMvc.perform(get("/")).andExpect(model().hasNoErrors());
	}


	@Controller
	private static class SampleController {

		private final Object[] values;

		public SampleController(Object... values) {
			this.values = values;
		}

		@RequestMapping("/")
		public String handle(Model model) {
			for (Object value : this.values) {
				model.addAttribute(value);
			}
			return "view";
		}

		@PostMapping("/persons")
		public String create(@Valid Person person, BindingResult result, Model model) {
			return "view";
		}
	}

	@ControllerAdvice
	private static class ModelAttributeAdvice {

		@ModelAttribute("globalAttrName")
		public String getAttribute() {
			return "Global Attribute Value";
		}
	}

}
