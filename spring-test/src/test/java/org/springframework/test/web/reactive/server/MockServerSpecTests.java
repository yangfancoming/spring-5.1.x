
package org.springframework.test.web.reactive.server;

import java.nio.charset.StandardCharsets;

import org.junit.Test;
import reactor.core.publisher.Mono;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.StringContains.*;

/**
 * Unit tests for {@link AbstractMockServerSpec}.
 * @author Rossen Stoyanchev
 */
public class MockServerSpecTests {

	private final TestMockServerSpec serverSpec = new TestMockServerSpec();


	@Test
	public void applyFiltersAfterConfigurerAdded() {

		this.serverSpec.webFilter(new TestWebFilter("A"));

		this.serverSpec.apply(new MockServerConfigurer() {

			@Override
			public void afterConfigureAdded(WebTestClient.MockServerSpec<?> spec) {
				spec.webFilter(new TestWebFilter("B"));
			}
		});

		this.serverSpec.build().get().uri("/")
				.exchange()
				.expectBody(String.class)
				.consumeWith(result -> assertThat(
						result.getResponseBody(), containsString("test-attribute=:A:B")));
	}

	@Test
	public void applyFiltersBeforeServerCreated() {

		this.serverSpec.webFilter(new TestWebFilter("App-A"));
		this.serverSpec.webFilter(new TestWebFilter("App-B"));

		this.serverSpec.apply(new MockServerConfigurer() {

			@Override
			public void beforeServerCreated(WebHttpHandlerBuilder builder) {
				builder.filters(filters -> {
					filters.add(0, new TestWebFilter("Fwk-A"));
					filters.add(1, new TestWebFilter("Fwk-B"));
				});
			}
		});

		this.serverSpec.build().get().uri("/")
				.exchange()
				.expectBody(String.class)
				.consumeWith(result -> assertThat(
						result.getResponseBody(), containsString("test-attribute=:Fwk-A:Fwk-B:App-A:App-B")));
	}


	private static class TestMockServerSpec extends AbstractMockServerSpec<TestMockServerSpec> {

		@Override
		protected WebHttpHandlerBuilder initHttpHandlerBuilder() {
			return WebHttpHandlerBuilder.webHandler(exchange -> {
				DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
				String text = exchange.getAttributes().toString();
				DataBuffer buffer = factory.wrap(text.getBytes(StandardCharsets.UTF_8));
				return exchange.getResponse().writeWith(Mono.just(buffer));
			});
		}
	}

	private static class TestWebFilter implements WebFilter {

		private final String name;

		TestWebFilter(String name) {
			this.name = name;
		}

		@Override
		public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
			String name = "test-attribute";
			String value = exchange.getAttributeOrDefault(name, "");
			exchange.getAttributes().put(name, value + ":" + this.name);
			return chain.filter(exchange);
		}
	}

}
