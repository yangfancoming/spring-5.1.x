
package org.springframework.test.web.reactive.server.samples;

import java.security.Principal;

import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Mono;

import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.lang.Nullable;
import org.springframework.test.web.reactive.server.MockServerConfigurer;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClientConfigurer;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;

/**
 * Samples tests that demonstrate applying ServerWebExchange initialization.
 * @author Rossen Stoyanchev
 */
public class ExchangeMutatorTests {

	private WebTestClient webTestClient;


	@Before
	public void setUp() throws Exception {

		this.webTestClient = WebTestClient.bindToController(new TestController())
				.apply(identity("Pablo"))
				.build();
	}

	@Test
	public void useGloballyConfiguredIdentity() throws Exception {
		this.webTestClient.get().uri("/userIdentity")
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class).isEqualTo("Hello Pablo!");
	}

	@Test
	public void useLocallyConfiguredIdentity() throws Exception {

		this.webTestClient
				.mutateWith(identity("Giovanni"))
				.get().uri("/userIdentity")
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class).isEqualTo("Hello Giovanni!");
	}


	private static IdentityConfigurer identity(String userName) {
		return new IdentityConfigurer(userName);
	}


	@RestController
	static class TestController {

		@GetMapping("/userIdentity")
		public String handle(Principal principal) {
			return "Hello " + principal.getName() + "!";
		}
	}

	private static class TestUser implements Principal {

		private final String name;

		TestUser(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return this.name;
		}
	}

	private static class IdentityConfigurer implements MockServerConfigurer, WebTestClientConfigurer {

		private final IdentityFilter filter;


		public IdentityConfigurer(String userName) {
			this.filter = new IdentityFilter(userName);
		}

		@Override
		public void beforeServerCreated(WebHttpHandlerBuilder builder) {
			builder.filters(filters -> filters.add(0, this.filter));
		}

		@Override
		public void afterConfigurerAdded(WebTestClient.Builder builder,
				@Nullable WebHttpHandlerBuilder httpHandlerBuilder,
				@Nullable ClientHttpConnector connector) {

			Assert.notNull(httpHandlerBuilder, "Not a mock server");
			httpHandlerBuilder.filters(filters -> {
				filters.removeIf(filter -> filter instanceof IdentityFilter);
				filters.add(0, this.filter);
			});
		}
	}

	private static class IdentityFilter implements WebFilter {

		private final Mono<Principal> userMono;


		IdentityFilter(String userName) {
			this.userMono = Mono.just(new TestUser(userName));
		}

		@Override
		public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
			exchange = exchange.mutate().principal(this.userMono).build();
			return chain.filter(exchange);
		}
	}

}
