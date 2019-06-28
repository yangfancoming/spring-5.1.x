

package org.springframework.http.server.reactive;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.*;

/**
 * @author Violeta Georgieva
 * @since 5.0
 */
public class WriteOnlyHandlerIntegrationTests extends AbstractHttpHandlerIntegrationTests {

	private static final int REQUEST_SIZE = 4096 * 3;

	private Random rnd = new Random();

	private byte[] body;


	@Override
	protected WriteOnlyHandler createHttpHandler() {
		return new WriteOnlyHandler();
	}

	@Test
	public void writeOnly() throws Exception {
		RestTemplate restTemplate = new RestTemplate();

		this.body = randomBytes();
		RequestEntity<byte[]> request = RequestEntity.post(
				new URI("http://localhost:" + port)).body(
						"".getBytes(StandardCharsets.UTF_8));
		ResponseEntity<byte[]> response = restTemplate.exchange(request, byte[].class);

		assertArrayEquals(body, response.getBody());
	}

	private byte[] randomBytes() {
		byte[] buffer = new byte[REQUEST_SIZE];
		rnd.nextBytes(buffer);
		return buffer;
	}


	public class WriteOnlyHandler implements HttpHandler {

		@Override
		public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
			DataBuffer buffer = response.bufferFactory().allocateBuffer(body.length);
			buffer.write(body);
			return response.writeAndFlushWith(Flux.just(Flux.just(buffer)));
		}
	}

}
