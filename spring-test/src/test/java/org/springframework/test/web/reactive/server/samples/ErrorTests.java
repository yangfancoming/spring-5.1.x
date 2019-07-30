

package org.springframework.test.web.reactive.server.samples;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.junit.Assert.*;

/**
 * Tests with error status codes or error conditions.
 *
 * @author Rossen Stoyanchev
 * @since 5.0
 */
public class ErrorTests {

	private final WebTestClient client = WebTestClient.bindToController(new TestController()).build();


	@Test
	public void notFound(){
		this.client.get().uri("/invalid")
				.exchange()
				.expectStatus().isNotFound()
				.expectBody(Void.class);
	}

	@Test
	public void serverException() {
		this.client.get().uri("/server-error")
				.exchange()
				.expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
				.expectBody(Void.class);
	}

	@Test // SPR-17363
	public void badRequestBeforeRequestBodyConsumed() {
		EntityExchangeResult<Void> result = this.client.post()
				.uri("/post")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.syncBody(new Person("Dan"))
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody().isEmpty();

		byte[] content = result.getRequestBodyContent();
		assertNotNull(content);
		assertEquals("{\"name\":\"Dan\"}", new String(content, StandardCharsets.UTF_8));
	}


	@RestController
	static class TestController {

		@GetMapping("/server-error")
		void handleAndThrowException() {
			throw new IllegalStateException("server error");
		}

		@PostMapping(path = "/post", params = "p")
		void handlePost(@RequestBody Person person) {
		}
	}

}