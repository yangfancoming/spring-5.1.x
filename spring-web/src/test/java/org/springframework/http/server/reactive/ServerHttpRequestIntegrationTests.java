

package org.springframework.http.server.reactive;

import java.net.URI;

import org.junit.Test;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.*;


public class ServerHttpRequestIntegrationTests extends AbstractHttpHandlerIntegrationTests {

	@Override
	protected CheckRequestHandler createHttpHandler() {
		return new CheckRequestHandler();
	}

	@Test
	public void checkUri() throws Exception {
		URI url = new URI("http://localhost:" + port + "/foo?param=bar");
		RequestEntity<Void> request = RequestEntity.post(url).build();
		ResponseEntity<Void> response = new RestTemplate().exchange(request, Void.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}


	public static class CheckRequestHandler implements HttpHandler {

		@Override
		public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
			URI uri = request.getURI();
			assertEquals("http", uri.getScheme());
			assertNotNull(uri.getHost());
			assertNotEquals(-1, uri.getPort());
			assertNotNull(request.getRemoteAddress());
			assertEquals("/foo", uri.getPath());
			assertEquals("param=bar", uri.getQuery());
			return Mono.empty();
		}
	}

}
