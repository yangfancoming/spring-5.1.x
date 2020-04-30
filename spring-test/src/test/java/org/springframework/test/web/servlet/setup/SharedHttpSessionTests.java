

package org.springframework.test.web.servlet.setup;

import javax.servlet.http.HttpSession;

import org.junit.Test;

import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.GetMapping;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.*;

/**
 * Tests for {@link SharedHttpSessionConfigurer}.
 *
 *
 */
public class SharedHttpSessionTests {

	@Test
	public void httpSession() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
				.apply(sharedHttpSession())
				.build();

		String url = "/session";

		MvcResult result = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();
		HttpSession session = result.getRequest().getSession(false);
		assertNotNull(session);
		assertEquals(1, session.getAttribute("counter"));

		result = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();
		session = result.getRequest().getSession(false);
		assertNotNull(session);
		assertEquals(2, session.getAttribute("counter"));

		result = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();
		session = result.getRequest().getSession(false);
		assertNotNull(session);
		assertEquals(3, session.getAttribute("counter"));
	}

	@Test
	public void noHttpSession() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
				.apply(sharedHttpSession())
				.build();

		String url = "/no-session";

		MvcResult result = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();
		HttpSession session = result.getRequest().getSession(false);
		assertNull(session);

		result = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();
		session = result.getRequest().getSession(false);
		assertNull(session);

		url = "/session";

		result = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();
		session = result.getRequest().getSession(false);
		assertNotNull(session);
		assertEquals(1, session.getAttribute("counter"));
	}


	@Controller
	private static class TestController {

		@GetMapping("/session")
		public String handle(HttpSession session) {
			Integer counter = (Integer) session.getAttribute("counter");
			session.setAttribute("counter", (counter != null ? counter + 1 : 1));
			return "view";
		}

		@GetMapping("/no-session")
		public String handle() {
			return "view";
		}
	}

}
