

package org.springframework.web.reactive.result.view.script;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.test.MockServerHttpResponse;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.web.reactive.result.view.ZeroDemandResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.adapter.DefaultServerWebExchange;
import org.springframework.web.server.i18n.AcceptHeaderLocaleContextResolver;
import org.springframework.web.server.session.DefaultWebSessionManager;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for pure JavaScript templates running on Nashorn engine.
 *
 * @author Sebastien Deleuze
 */
public class NashornScriptTemplateTests {

	@Test
	public void renderTemplate() throws Exception {
		Map<String, Object> model = new HashMap<>();
		model.put("title", "Layout example");
		model.put("body", "This is the body");
		String url = "org/springframework/web/reactive/result/view/script/nashorn/template.html";
		MockServerHttpResponse response = render(url, model, ScriptTemplatingConfiguration.class);
		assertEquals("<html><head><title>Layout example</title></head><body><p>This is the body</p></body></html>",
				response.getBodyAsString().block());
	}

	@Test  // SPR-13453
	public void renderTemplateWithUrl() throws Exception {
		String url = "org/springframework/web/reactive/result/view/script/nashorn/template.html";
		Class<?> configClass = ScriptTemplatingWithUrlConfiguration.class;
		MockServerHttpResponse response = render(url, null, configClass);
		assertEquals("<html><head><title>Check url parameter</title></head><body><p>" + url + "</p></body></html>",
				response.getBodyAsString().block());
	}

	@Test // gh-22754
	public void subscribeWithoutDemand() throws Exception {
		ZeroDemandResponse response = new ZeroDemandResponse();
		ServerWebExchange exchange = new DefaultServerWebExchange(
				MockServerHttpRequest.get("/path").build(), response,
				new DefaultWebSessionManager(), ServerCodecConfigurer.create(),
				new AcceptHeaderLocaleContextResolver());

		Map<String, Object> model = new HashMap<>();
		model.put("title", "Layout example");
		model.put("body", "This is the body");
		String viewUrl = "org/springframework/web/reactive/result/view/script/nashorn/template.html";
		ScriptTemplateView view = createViewWithUrl(viewUrl, ScriptTemplatingConfiguration.class);
		view.render(model, null, exchange).subscribe();

		response.cancelWrite();
		response.checkForLeaks();
	}

	private MockServerHttpResponse render(String viewUrl, Map<String, Object> model,
			Class<?> configuration) throws Exception {

		ScriptTemplateView view = createViewWithUrl(viewUrl, configuration);
		MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));
		view.renderInternal(model, MediaType.TEXT_HTML, exchange).block();
		return exchange.getResponse();
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
		public ScriptTemplateConfigurer nashornConfigurer() {
			ScriptTemplateConfigurer configurer = new ScriptTemplateConfigurer();
			configurer.setEngineName("nashorn");
			configurer.setScripts("org/springframework/web/reactive/result/view/script/nashorn/render.js");
			configurer.setRenderFunction("render");
			return configurer;
		}
	}


	@Configuration
	static class ScriptTemplatingWithUrlConfiguration {

		@Bean
		public ScriptTemplateConfigurer nashornConfigurer() {
			ScriptTemplateConfigurer configurer = new ScriptTemplateConfigurer();
			configurer.setEngineName("nashorn");
			configurer.setScripts("org/springframework/web/reactive/result/view/script/nashorn/render.js");
			configurer.setRenderFunction("renderWithUrl");
			return configurer;
		}
	}

}
