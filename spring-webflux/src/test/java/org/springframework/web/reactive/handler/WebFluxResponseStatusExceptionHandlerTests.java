

package org.springframework.web.reactive.handler;

import java.time.Duration;

import org.junit.Test;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.handler.WebFluxResponseStatusExceptionHandler;
import org.springframework.web.server.handler.ResponseStatusExceptionHandler;
import org.springframework.web.server.handler.ResponseStatusExceptionHandlerTests;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link WebFluxResponseStatusExceptionHandler}.
 *

 *
 */
public class WebFluxResponseStatusExceptionHandlerTests extends ResponseStatusExceptionHandlerTests {

	@Override
	protected ResponseStatusExceptionHandler createResponseStatusExceptionHandler() {
		return new WebFluxResponseStatusExceptionHandler();
	}


	@Test
	public void handleAnnotatedException() {
		Throwable ex = new CustomException();
		this.handler.handle(this.exchange, ex).block(Duration.ofSeconds(5));
		assertEquals(HttpStatus.I_AM_A_TEAPOT, this.exchange.getResponse().getStatusCode());
	}

	@Test
	public void handleNestedAnnotatedException() {
		Throwable ex = new Exception(new CustomException());
		this.handler.handle(this.exchange, ex).block(Duration.ofSeconds(5));
		assertEquals(HttpStatus.I_AM_A_TEAPOT, this.exchange.getResponse().getStatusCode());
	}


	@SuppressWarnings("serial")
	@ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
	private static class CustomException extends Exception {
	}

}
