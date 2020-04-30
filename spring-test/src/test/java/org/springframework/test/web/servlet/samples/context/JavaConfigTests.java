

package org.springframework.test.web.servlet.samples.context;

import javax.servlet.ServletContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.Person;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.samples.context.JavaConfigTests.RootConfig;
import org.springframework.test.web.servlet.samples.context.JavaConfigTests.WebConfig;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests with Java configuration.
 *
 *
 * @author Sam Brannen
 * @author Sebastien Deleuze
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration("classpath:META-INF/web-resources")
@ContextHierarchy({
	@ContextConfiguration(classes = RootConfig.class),
	@ContextConfiguration(classes = WebConfig.class)
})
public class JavaConfigTests {

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private PersonDao personDao;

	@Autowired
	private PersonController personController;

	private MockMvc mockMvc;


	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		verifyRootWacSupport();
		given(this.personDao.getPerson(5L)).willReturn(new Person("Joe"));
	}

	@Test
	public void person() throws Exception {
		this.mockMvc.perform(get("/person/5").accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().string("{\"name\":\"Joe\",\"someDouble\":0.0,\"someBoolean\":false}"));
	}

	@Test
	public void tilesDefinitions() throws Exception {
		this.mockMvc.perform(get("/"))
			.andExpect(status().isOk())
			.andExpect(forwardedUrl("/WEB-INF/layouts/standardLayout.jsp"));
	}

	/**
	 * Verify that the breaking change introduced in <a
	 * href="https://jira.spring.io/browse/SPR-12553">SPR-12553</a> has been reverted.
	 *
	 * This code has been copied from
	 * {@link org.springframework.test.context.hierarchies.web.ControllerIntegrationTests}.
	 *
	 * @see org.springframework.test.context.hierarchies.web.ControllerIntegrationTests#verifyRootWacSupport()
	 */
	private void verifyRootWacSupport() {
		assertNotNull(personDao);
		assertNotNull(personController);

		ApplicationContext parent = wac.getParent();
		assertNotNull(parent);
		assertTrue(parent instanceof WebApplicationContext);
		WebApplicationContext root = (WebApplicationContext) parent;

		ServletContext childServletContext = wac.getServletContext();
		assertNotNull(childServletContext);
		ServletContext rootServletContext = root.getServletContext();
		assertNotNull(rootServletContext);
		assertSame(childServletContext, rootServletContext);

		assertSame(root, rootServletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE));
		assertSame(root, childServletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE));
	}


	@Configuration
	static class RootConfig {

		@Bean
		public PersonDao personDao() {
			return Mockito.mock(PersonDao.class);
		}
	}

	@Configuration
	@EnableWebMvc
	static class WebConfig implements WebMvcConfigurer {

		@Autowired
		private RootConfig rootConfig;

		@Bean
		public PersonController personController() {
			return new PersonController(this.rootConfig.personDao());
		}

		@Override
		public void addResourceHandlers(ResourceHandlerRegistry registry) {
			registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
		}

		@Override
		public void addViewControllers(ViewControllerRegistry registry) {
			registry.addViewController("/").setViewName("home");
		}

		@Override
		public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
			configurer.enable();
		}

		@Override
		public void configureViewResolvers(ViewResolverRegistry registry) {
			registry.tiles();
		}

		@Bean
		public TilesConfigurer tilesConfigurer() {
			TilesConfigurer configurer = new TilesConfigurer();
			configurer.setDefinitions("/WEB-INF/**/tiles.xml");
			return configurer;
		}
	}

}
