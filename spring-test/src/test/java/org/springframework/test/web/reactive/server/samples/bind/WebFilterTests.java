

package org.springframework.test.web.reactive.server.samples.bind;

import java.nio.charset.StandardCharsets;

import org.junit.Test;
import reactor.core.publisher.Mono;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.WebFilter;

/**
 * Tests for a {@link WebFilter}.
 *
 */
public class WebFilterTests {

	@Test
	public void testWebFilter() throws Exception {

		WebFilter filter = (exchange, chain) -> {
			DataBuffer buffer = new DefaultDataBufferFactory().allocateBuffer();
			buffer.write("It works!".getBytes(StandardCharsets.UTF_8));
			return exchange.getResponse().writeWith(Mono.just(buffer));
		};

		WebTestClient client = WebTestClient.bindToWebHandler(exchange -> Mono.empty())
				.webFilter(filter)
				.build();

		client.get().uri("/")
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class).isEqualTo("It works!");
	}

}
