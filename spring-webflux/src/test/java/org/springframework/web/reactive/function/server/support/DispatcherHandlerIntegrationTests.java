

package org.springframework.web.reactive.function.server.support;

import org.junit.Test;
import reactor.core.publisher.Mono;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.AbstractHttpHandlerIntegrationTests;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;

import static org.junit.Assert.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.*;


public class DispatcherHandlerIntegrationTests extends AbstractHttpHandlerIntegrationTests {

	private final RestTemplate restTemplate = new RestTemplate();


	@Override
	protected HttpHandler createHttpHandler() {
		AnnotationConfigApplicationContext wac = new AnnotationConfigApplicationContext();
		wac.register(TestConfiguration.class);
		wac.refresh();

		return WebHttpHandlerBuilder.webHandler(new DispatcherHandler(wac)).build();
	}


	@Test
	public void nested() {
		ResponseEntity<String> result = this.restTemplate
				.getForEntity("http://localhost:" + this.port + "/foo/bar", String.class);

		assertEquals(200, result.getStatusCodeValue());
	}


	@Configuration
	@EnableWebFlux
	static class TestConfiguration {

		@Bean
		public RouterFunction<ServerResponse> router(Handler handler) {
			return route()
					.path("/foo", () -> route()
							.nest(accept(MediaType.APPLICATION_JSON), builder -> builder
									.GET("/bar", handler::handle))
							.build())
					.build();
		}

		@Bean
		public Handler handler() {
			return new Handler();
		}
	}


	static class Handler {

		public Mono<ServerResponse> handle(ServerRequest request) {
			return ServerResponse.ok().build();
		}
	}

}
