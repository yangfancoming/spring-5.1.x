

package org.springframework.web.reactive.function.client;

import java.nio.charset.Charset;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.lang.Nullable;

/**
 * Exception thrown when an unknown (or custom) HTTP status code is received.
 *
 * @author Brian Clozel
 * @since 5.1
 */
public class UnknownHttpStatusCodeException extends WebClientResponseException {

	private static final long serialVersionUID = 2407169540168185007L;


	/**
	 * Create a new instance of the {@code UnknownHttpStatusCodeException} with the given
	 * parameters.
	 */
	public UnknownHttpStatusCodeException(
			int statusCode, HttpHeaders headers, byte[] responseBody, Charset responseCharset) {

		super("Unknown status code [" + statusCode + "]", statusCode, "",
				headers, responseBody, responseCharset);
	}

	/**
	 * Create a new instance of the {@code UnknownHttpStatusCodeException} with the given
	 * parameters.
	 * @since 5.1.4
	 */
	public UnknownHttpStatusCodeException(
			int statusCode, HttpHeaders headers, byte[] responseBody, Charset responseCharset,
			@Nullable HttpRequest request) {

		super("Unknown status code [" + statusCode + "]", statusCode, "",
				headers, responseBody, responseCharset, request);
	}

}
