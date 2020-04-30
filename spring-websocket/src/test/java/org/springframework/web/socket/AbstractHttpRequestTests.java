

package org.springframework.web.socket;

import org.junit.Before;

import org.springframework.http.server.ServerHttpAsyncRequestControl;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;

/**
 * Base class for tests using {@link ServerHttpRequest} and {@link ServerHttpResponse}.
 *
 *
 */
public abstract class AbstractHttpRequestTests {

	protected ServerHttpRequest request;

	protected ServerHttpResponse response;

	protected MockHttpServletRequest servletRequest;

	protected MockHttpServletResponse servletResponse;

	protected ServerHttpAsyncRequestControl asyncControl;


	@Before
	public void setup() {
		resetRequestAndResponse();
	}

	protected void setRequest(String method, String requestUri) {
		this.servletRequest.setMethod(method);
		this.servletRequest.setRequestURI(requestUri);
		this.request = new ServletServerHttpRequest(this.servletRequest);
	}

	protected void resetRequestAndResponse() {
		resetRequest();
		resetResponse();
		this.asyncControl = this.request.getAsyncRequestControl(this.response);
	}

	protected void resetRequest() {
		this.servletRequest = new MockHttpServletRequest();
		this.servletRequest.setAsyncSupported(true);
		this.request = new ServletServerHttpRequest(this.servletRequest);
	}

	protected void resetResponse() {
		this.servletResponse = new MockHttpServletResponse();
		this.response = new ServletServerHttpResponse(this.servletResponse);
	}

}
