
package org.springframework.web.reactive.result.method.annotation;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.bootstrap.ReactorHttpServer;
import org.springframework.http.server.reactive.bootstrap.TomcatHttpServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;

import static org.junit.Assert.assertEquals;

/**
 * Integration tests related to the use of context paths.
 *
 * @author Rossen Stoyanchev
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class ContextPathIntegrationTests {


	@Test
	public void multipleWebFluxApps() throws Exception {
		AnnotationConfigApplicationContext context1 = new AnnotationConfigApplicationContext();
		context1.register(WebAppConfig.class);
		context1.refresh();

		AnnotationConfigApplicationContext context2 = new AnnotationConfigApplicationContext();
		context2.register(WebAppConfig.class);
		context2.refresh();

		HttpHandler webApp1Handler = WebHttpHandlerBuilder.applicationContext(context1).build();
		HttpHandler webApp2Handler = WebHttpHandlerBuilder.applicationContext(context2).build();

		ReactorHttpServer server = new ReactorHttpServer();
		server.registerHttpHandler("/webApp1", webApp1Handler);
		server.registerHttpHandler("/webApp2", webApp2Handler);
		server.afterPropertiesSet();
		server.start();

		try {
			RestTemplate restTemplate = new RestTemplate();
			String actual;

			String url = "http://localhost:" + server.getPort() + "/webApp1/test";
			actual = restTemplate.getForObject(url, String.class);
			assertEquals("Tested in /webApp1", actual);

			url = "http://localhost:" + server.getPort() + "/webApp2/test";
			actual = restTemplate.getForObject(url, String.class);
			assertEquals("Tested in /webApp2", actual);
		}
		finally {
			server.stop();
		}
	}

	@Test
	public void servletPathMapping() throws Exception {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(WebAppConfig.class);
		context.refresh();

		File base = new File(System.getProperty("java.io.tmpdir"));
		TomcatHttpServer server = new TomcatHttpServer(base.getAbsolutePath());
		server.setContextPath("/app");
		server.setServletMapping("/api/*");

		HttpHandler httpHandler = WebHttpHandlerBuilder.applicationContext(context).build();
		server.setHandler(httpHandler);

		server.afterPropertiesSet();
		server.start();

		try {
			RestTemplate restTemplate = new RestTemplate();
			String actual;

			String url = "http://localhost:" + server.getPort() + "/app/api/test";
			actual = restTemplate.getForObject(url, String.class);
			assertEquals("Tested in /app/api", actual);
		}
		finally {
			server.stop();
		}
	}



	@EnableWebFlux
	@Configuration
	static class WebAppConfig {

		@Bean
		public TestController testController() {
			return new TestController();
		}
	}


	@RestController
	static class TestController {

		@GetMapping("/test")
		public String handle(ServerHttpRequest request) {
			return "Tested in " + request.getPath().contextPath().value();
		}
	}

}
