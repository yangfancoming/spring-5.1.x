

package org.springframework.test.web.servlet.samples.standalone.resultmatchers;

import org.junit.Before;
import org.junit.Test;

import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

/**
 * Examples of expectations on response cookies values.
 *
 *
 * @author Nikola Yovchev
 */
public class CookieAssertionTests {

	private static final String COOKIE_NAME = CookieLocaleResolver.DEFAULT_COOKIE_NAME;

	private MockMvc mockMvc;


	@Before
	public void setup() {
		CookieLocaleResolver localeResolver = new CookieLocaleResolver();
		localeResolver.setCookieDomain("domain");
		localeResolver.setCookieHttpOnly(true);

		this.mockMvc = standaloneSetup(new SimpleController())
				.addInterceptors(new LocaleChangeInterceptor())
				.setLocaleResolver(localeResolver)
				.defaultRequest(get("/").param("locale", "en_US"))
				.alwaysExpect(status().isOk())
				.build();
	}


	@Test
	public void testExists() throws Exception {
		this.mockMvc.perform(get("/")).andExpect(cookie().exists(COOKIE_NAME));
	}

	@Test
	public void testNotExists() throws Exception {
		this.mockMvc.perform(get("/")).andExpect(cookie().doesNotExist("unknownCookie"));
	}

	@Test
	public void testEqualTo() throws Exception {
		this.mockMvc.perform(get("/")).andExpect(cookie().value(COOKIE_NAME, "en-US"));
		this.mockMvc.perform(get("/")).andExpect(cookie().value(COOKIE_NAME, equalTo("en-US")));
	}

	@Test
	public void testMatcher() throws Exception {
		this.mockMvc.perform(get("/")).andExpect(cookie().value(COOKIE_NAME, startsWith("en")));
	}

	@Test
	public void testMaxAge() throws Exception {
		this.mockMvc.perform(get("/")).andExpect(cookie().maxAge(COOKIE_NAME, -1));
	}

	@Test
	public void testDomain() throws Exception {
		this.mockMvc.perform(get("/")).andExpect(cookie().domain(COOKIE_NAME, "domain"));
	}

	@Test
	public void testVersion() throws Exception {
		this.mockMvc.perform(get("/")).andExpect(cookie().version(COOKIE_NAME, 0));
	}

	@Test
	public void testPath() throws Exception {
		this.mockMvc.perform(get("/")).andExpect(cookie().path(COOKIE_NAME, "/"));
	}

	@Test
	public void testSecured() throws Exception {
		this.mockMvc.perform(get("/")).andExpect(cookie().secure(COOKIE_NAME, false));
	}

	@Test
	public void testHttpOnly() throws Exception {
		this.mockMvc.perform(get("/")).andExpect(cookie().httpOnly(COOKIE_NAME, true));
	}


	@Controller
	private static class SimpleController {

		@RequestMapping("/")
		public String home() {
			return "home";
		}
	}

}
