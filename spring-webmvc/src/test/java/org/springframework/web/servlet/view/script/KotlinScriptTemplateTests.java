

package org.springframework.web.servlet.view.script;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletContext;

import org.junit.Before;
import org.junit.Test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Kotlin script templates running on Kotlin JSR-223 support.
 *
 * @author Sebastien Deleuze
 */
public class KotlinScriptTemplateTests {

	private WebApplicationContext webAppContext;

	private ServletContext servletContext;


	@Before
	public void setup() {
		this.webAppContext = mock(WebApplicationContext.class);
		this.servletContext = new MockServletContext();
		this.servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.webAppContext);
	}


	@Test
	public void renderTemplateWithFrenchLocale() throws Exception {
		Map<String, Object> model = new HashMap<>();
		model.put("foo", "Foo");
		String url = "org/springframework/web/servlet/view/script/kotlin/template.kts";
		MockHttpServletResponse response = render(url, model, Locale.FRENCH, ScriptTemplatingConfiguration.class);
		assertEquals("<html><body>\n<p>Bonjour Foo</p>\n</body></html>", response.getContentAsString());
	}

	@Test
	public void renderTemplateWithEnglishLocale() throws Exception {
		Map<String, Object> model = new HashMap<>();
		model.put("foo", "Foo");
		String url = "org/springframework/web/servlet/view/script/kotlin/template.kts";
		MockHttpServletResponse response = render(url, model, Locale.ENGLISH, ScriptTemplatingConfiguration.class);
		assertEquals("<html><body>\n<p>Hello Foo</p>\n</body></html>", response.getContentAsString());
	}

	@Test
	public void renderTemplateWithoutRenderFunction() throws Exception {
		Map<String, Object> model = new HashMap<>();
		model.put("header", "<html><body>");
		model.put("hello", "Hello");
		model.put("foo", "Foo");
		model.put("footer", "</body></html>");
		MockHttpServletResponse response = render("org/springframework/web/servlet/view/script/kotlin/eval.kts",
				model, Locale.ENGLISH, ScriptTemplatingConfigurationWithoutRenderFunction.class);
		assertEquals("<html><body>\n<p>Hello Foo</p>\n</body></html>",
				response.getContentAsString());
	}


	private MockHttpServletResponse render(String viewUrl, Map<String, Object> model,
			Locale locale, Class<?> configuration) throws Exception {

		ScriptTemplateView view = createViewWithUrl(viewUrl, configuration);
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addPreferredLocale(locale);
		view.renderMergedOutputModel(model, request, response);
		return response;
	}

	private ScriptTemplateView createViewWithUrl(String viewUrl, Class<?> configuration) throws Exception {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(configuration);
		ctx.refresh();

		ScriptTemplateView view = new ScriptTemplateView();
		view.setApplicationContext(ctx);
		view.setUrl(viewUrl);
		view.afterPropertiesSet();
		return view;
	}


	@Configuration
	static class ScriptTemplatingConfiguration {

		@Bean
		public ScriptTemplateConfigurer kotlinScriptConfigurer() {
			ScriptTemplateConfigurer configurer = new ScriptTemplateConfigurer();
			configurer.setEngineName("kotlin");
			configurer.setScripts("org/springframework/web/servlet/view/script/kotlin/render.kts");
			configurer.setRenderFunction("render");
			return configurer;
		}

		@Bean
		public ResourceBundleMessageSource messageSource() {
			ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
			messageSource.setBasename("org/springframework/web/servlet/view/script/messages");
			return messageSource;
		}
	}

	@Configuration
	static class ScriptTemplatingConfigurationWithoutRenderFunction {

		@Bean
		public ScriptTemplateConfigurer kotlinScriptConfigurer() {
			return new ScriptTemplateConfigurer("kotlin");
		}
	}

}
