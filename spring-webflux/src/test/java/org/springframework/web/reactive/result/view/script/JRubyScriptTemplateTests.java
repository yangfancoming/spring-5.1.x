

package org.springframework.web.reactive.result.view.script;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.test.MockServerHttpResponse;
import org.springframework.mock.web.test.server.MockServerWebExchange;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for ERB templates running on JRuby.
 *
 * @author Sebastien Deleuze
 */
@Ignore("JRuby not compatible with JDK 9 yet")
public class JRubyScriptTemplateTests {

	@Test
	public void renderTemplate() throws Exception {
		Map<String, Object> model = new HashMap<>();
		model.put("title", "Layout example");
		model.put("body", "This is the body");
		String url = "org/springframework/web/reactive/result/view/script/jruby/template.erb";
		MockServerHttpResponse response = renderViewWithModel(url, model);
		assertEquals("<html><head><title>Layout example</title></head><body>This is the body</p></body></html>",
				response.getBodyAsString().block());
	}

	private MockServerHttpResponse renderViewWithModel(String viewUrl, Map<String, Object> model) throws Exception {
		ScriptTemplateView view = createViewWithUrl(viewUrl);
		MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));
		view.renderInternal(model, MediaType.TEXT_HTML, exchange).block();
		return exchange.getResponse();
	}

	private ScriptTemplateView createViewWithUrl(String viewUrl) throws Exception {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(ScriptTemplatingConfiguration.class);
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
		public ScriptTemplateConfigurer jRubyConfigurer() {
			ScriptTemplateConfigurer configurer = new ScriptTemplateConfigurer();
			configurer.setScripts("org/springframework/web/reactive/result/view/script/jruby/render.rb");
			configurer.setEngineName("jruby");
			configurer.setRenderFunction("render");
			return configurer;
		}
	}

}
