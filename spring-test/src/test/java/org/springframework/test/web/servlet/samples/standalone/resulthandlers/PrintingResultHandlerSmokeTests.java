

package org.springframework.test.web.servlet.samples.standalone.resulthandlers;

import java.io.StringWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.junit.Ignore;
import org.junit.Test;

import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.result.PrintingResultHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

/**
 * Smoke test for {@link PrintingResultHandler}.
 *
 * Prints debugging information about the executed request and response to
 * various output streams.
 *
 * <strong>NOTE</strong>: this <em>smoke test</em> is not intended to be
 * executed with the build. To run this test, comment out the {@code @Ignore}
 * declaration and inspect the output manually.
 *
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 * @see org.springframework.test.web.servlet.result.PrintingResultHandlerTests
 */
@Ignore("Not intended to be executed with the build. Comment out this line to inspect the output manually.")
public class PrintingResultHandlerSmokeTests {

	@Test
	public void testPrint() throws Exception {
		StringWriter writer = new StringWriter();

		standaloneSetup(new SimpleController())
			.build()
			.perform(get("/").content("Hello Request".getBytes()))
			.andDo(log())
			.andDo(print())
			.andDo(print(System.err))
			.andDo(print(writer))
		;

		System.out.println();
		System.out.println("===============================================================");
		System.out.println(writer.toString());
	}


	@Controller
	private static class SimpleController {

		@RequestMapping("/")
		@ResponseBody
		public String hello(HttpServletResponse response) {
			response.addCookie(new Cookie("enigma", "42"));
			return "Hello Response";
		}
	}
}
