

package org.springframework.web.server.handler;

import java.time.Duration;

import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link ResponseStatusExceptionHandler}.
 *
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 */
public class ResponseStatusExceptionHandlerTests {

	protected final MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));

	protected ResponseStatusExceptionHandler handler;


	@Before
	public void setup() {
		this.handler = createResponseStatusExceptionHandler();
	}

	protected ResponseStatusExceptionHandler createResponseStatusExceptionHandler() {
		return new ResponseStatusExceptionHandler();
	}


	@Test
	public void handleResponseStatusException() {
		Throwable ex = new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
		this.handler.handle(this.exchange, ex).block(Duration.ofSeconds(5));
		assertEquals(HttpStatus.BAD_REQUEST, this.exchange.getResponse().getStatusCode());
	}

	@Test
	public void handleNestedResponseStatusException() {
		Throwable ex = new Exception(new ResponseStatusException(HttpStatus.BAD_REQUEST, ""));
		this.handler.handle(this.exchange, ex).block(Duration.ofSeconds(5));
		assertEquals(HttpStatus.BAD_REQUEST, this.exchange.getResponse().getStatusCode());
	}

	@Test
	public void unresolvedException() {
		Throwable expected = new IllegalStateException();
		Mono<Void> mono = this.handler.handle(this.exchange, expected);
		StepVerifier.create(mono).consumeErrorWith(actual -> assertSame(expected, actual)).verify();
	}

	@Test  // SPR-16231
	public void responseCommitted() {
		Throwable ex = new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops");
		this.exchange.getResponse().setStatusCode(HttpStatus.CREATED);
		Mono<Void> mono = this.exchange.getResponse().setComplete()
				.then(Mono.defer(() -> this.handler.handle(this.exchange, ex)));
		StepVerifier.create(mono).consumeErrorWith(actual -> assertSame(ex, actual)).verify();
	}

}
