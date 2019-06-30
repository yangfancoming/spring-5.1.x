

package org.springframework.test.web.servlet.samples.context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests dependent on access to resources under the web application root directory.
 *
 * @author Rossen Stoyanchev
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration("src/test/resources/META-INF/web-resources")
@ContextHierarchy({
	@ContextConfiguration("root-context.xml"),
	@ContextConfiguration("servlet-context.xml")
})
public class WebAppResourceTests {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).alwaysExpect(status().isOk()).build();
	}

	// TilesConfigurer: resources under "/WEB-INF/**/tiles.xml"

	@Test
	public void tilesDefinitions() throws Exception {
		this.mockMvc.perform(get("/"))
			.andExpect(forwardedUrl("/WEB-INF/layouts/standardLayout.jsp"));
	}

	// Resources served via <mvc:resources/>

	@Test
	public void resourceRequest() throws Exception {
		this.mockMvc.perform(get("/resources/Spring.js"))
			.andExpect(content().contentType("application/javascript"))
			.andExpect(content().string(containsString("Spring={};")));
	}

	// Forwarded to the "default" servlet via <mvc:default-servlet-handler/>

	@Test
	public void resourcesViaDefaultServlet() throws Exception {
		this.mockMvc.perform(get("/unknown/resource"))
			.andExpect(handler().handlerType(DefaultServletHttpRequestHandler.class))
			.andExpect(forwardedUrl("default"));
	}

}
