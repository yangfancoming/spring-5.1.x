

package org.springframework.http.client.support;

import java.io.IOException;
import java.nio.charset.Charset;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * {@link ClientHttpRequestInterceptor} to apply a given HTTP Basic Authentication
 * username/password pair, unless a custom Authorization header has been set before.
 *

 * @since 5.1.1
 * @see HttpHeaders#setBasicAuth
 * @see HttpHeaders#AUTHORIZATION
 */
public class BasicAuthenticationInterceptor implements ClientHttpRequestInterceptor {

	private final String username;

	private final String password;

	@Nullable
	private final Charset charset;


	/**
	 * Create a new interceptor which adds Basic Authentication for the
	 * given username and password.
	 * @param username the username to use
	 * @param password the password to use
	 * @see HttpHeaders#setBasicAuth(String, String)
	 */
	public BasicAuthenticationInterceptor(String username, String password) {
		this(username, password, null);
	}

	/**
	 * Create a new interceptor which adds Basic Authentication for the
	 * given username and password, encoded using the specified charset.
	 * @param username the username to use
	 * @param password the password to use
	 * @param charset the charset to use
	 * @see HttpHeaders#setBasicAuth(String, String, Charset)
	 */
	public BasicAuthenticationInterceptor(String username, String password, @Nullable Charset charset) {
		Assert.doesNotContain(username, ":", "Username must not contain a colon");
		this.username = username;
		this.password = password;
		this.charset = charset;
	}


	@Override
	public ClientHttpResponse intercept(
			HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

		HttpHeaders headers = request.getHeaders();
		if (!headers.containsKey(HttpHeaders.AUTHORIZATION)) {
			headers.setBasicAuth(this.username, this.password, this.charset);
		}
		return execution.execute(request, body);
	}

}
