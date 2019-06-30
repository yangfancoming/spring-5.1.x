

package org.springframework.test.web.servlet.samples.standalone;

import org.junit.Test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

/**
 * Exception handling via {@code @ExceptionHandler} method.
 *
 * @author Rossen Stoyanchev
 */
public class ExceptionHandlerTests {

	@Test
	public void testExceptionHandlerMethod() throws Exception {
		standaloneSetup(new PersonController()).build()
			.perform(get("/person/Clyde"))
				.andExpect(status().isOk())
				.andExpect(forwardedUrl("errorView"));
	}

	@Test
	public void testGlobalExceptionHandlerMethod() throws Exception {
		standaloneSetup(new PersonController()).setControllerAdvice(new GlobalExceptionHandler()).build()
				.perform(get("/person/Bonnie"))
				.andExpect(status().isOk())
				.andExpect(forwardedUrl("globalErrorView"));
	}

	@Test
	public void testGlobalExceptionHandlerMethodUsingClassArgument() throws Exception {
		standaloneSetup(PersonController.class).setControllerAdvice(GlobalExceptionHandler.class).build()
				.perform(get("/person/Bonnie"))
				.andExpect(status().isOk())
				.andExpect(forwardedUrl("globalErrorView"));
	}


	@Controller
	private static class PersonController {

		@GetMapping("/person/{name}")
		public String show(@PathVariable String name) {
			if (name.equals("Clyde")) {
				throw new IllegalArgumentException("simulated exception");
			}
			else if (name.equals("Bonnie")) {
				throw new IllegalStateException("simulated exception");
			}
			return "person/show";
		}

		@ExceptionHandler
		public String handleException(IllegalArgumentException exception) {
			return "errorView";
		}
	}


	@ControllerAdvice
	private static class GlobalExceptionHandler {

		@ExceptionHandler
		public String handleException(IllegalStateException exception) {
			return "globalErrorView";
		}

	}

}
