

package org.springframework.http.server.reactive;

import java.net.URI;
import java.time.Duration;

import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.*;

/**
 * @author Stephane Maldini
 * @since 5.0
 */
public class AsyncIntegrationTests extends AbstractHttpHandlerIntegrationTests {

	private final Scheduler asyncGroup = Schedulers.parallel();

	private final DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();


	@Override
	protected AsyncHandler createHttpHandler() {
		return new AsyncHandler();
	}

	@Test
	@Ignore  // TODO: fragile due to socket failures
	public void basicTest() throws Exception {
		URI url = new URI("http://localhost:" + port);
		ResponseEntity<String> response = new RestTemplate().exchange(
				RequestEntity.get(url).build(), String.class);

		assertThat(response.getBody(), Matchers.equalTo("hello"));
	}


	private class AsyncHandler implements HttpHandler {

		@Override
		public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
			return response.writeWith(Flux.just("h", "e", "l", "l", "o")
										.delayElements(Duration.ofMillis(100))
										.publishOn(asyncGroup)
					.collect(dataBufferFactory::allocateBuffer, (buffer, str) -> buffer.write(str.getBytes())));
		}
	}

}
