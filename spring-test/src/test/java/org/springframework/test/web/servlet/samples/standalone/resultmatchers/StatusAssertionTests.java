

package org.springframework.test.web.servlet.samples.standalone.resultmatchers;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.Test;

import org.springframework.core.annotation.AliasFor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

/**
 * Examples of expectations on the status and the status reason found in the response.
 *
 *
 * @author Sam Brannen
 */
public class StatusAssertionTests {

	private final MockMvc mockMvc = standaloneSetup(new StatusController()).build();

	@Test
	public void testStatusInt() throws Exception {
		this.mockMvc.perform(get("/created")).andExpect(status().is(201));
		this.mockMvc.perform(get("/createdWithComposedAnnotation")).andExpect(status().is(201));
		this.mockMvc.perform(get("/badRequest")).andExpect(status().is(400));
	}

	@Test
	public void testHttpStatus() throws Exception {
		this.mockMvc.perform(get("/created")).andExpect(status().isCreated());
		this.mockMvc.perform(get("/createdWithComposedAnnotation")).andExpect(status().isCreated());
		this.mockMvc.perform(get("/badRequest")).andExpect(status().isBadRequest());
	}

	@Test
	public void testMatcher() throws Exception {
		this.mockMvc.perform(get("/badRequest")).andExpect(status().is(equalTo(400)));
	}

	@Test
	public void testReasonEqualTo() throws Exception {
		this.mockMvc.perform(get("/badRequest")).andExpect(status().reason("Expired token"));

		// Hamcrest matchers...
		this.mockMvc.perform(get("/badRequest")).andExpect(status().reason(equalTo("Expired token")));
	}

	@Test
	public void testReasonMatcher() throws Exception {
		this.mockMvc.perform(get("/badRequest")).andExpect(status().reason(endsWith("token")));
	}


	@RequestMapping
	@ResponseStatus
	@Retention(RetentionPolicy.RUNTIME)
	@interface Get {

		@AliasFor(annotation = RequestMapping.class, attribute = "path")
		String[] path() default {};

		@AliasFor(annotation = ResponseStatus.class, attribute = "code")
		HttpStatus status() default INTERNAL_SERVER_ERROR;
	}

	@Controller
	private static class StatusController {

		@RequestMapping("/created")
		@ResponseStatus(CREATED)
		public @ResponseBody void created(){
		}

		@Get(path = "/createdWithComposedAnnotation", status = CREATED)
		public @ResponseBody void createdWithComposedAnnotation() {
		}

		@RequestMapping("/badRequest")
		@ResponseStatus(code = BAD_REQUEST, reason = "Expired token")
		public @ResponseBody void badRequest(){
		}

		@RequestMapping("/notImplemented")
		@ResponseStatus(NOT_IMPLEMENTED)
		public @ResponseBody void notImplemented(){
		}
	}

}
