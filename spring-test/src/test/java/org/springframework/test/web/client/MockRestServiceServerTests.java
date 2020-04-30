

package org.springframework.test.web.client;

import java.net.SocketException;

import org.junit.Test;

import org.springframework.test.web.client.MockRestServiceServer.MockRestServiceServerBuilder;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * Unit tests for {@link MockRestServiceServer}.
 *
 *
 */
public class MockRestServiceServerTests {

	private final RestTemplate restTemplate = new RestTemplate();


	@Test
	public void buildMultipleTimes() {
		MockRestServiceServerBuilder builder = MockRestServiceServer.bindTo(this.restTemplate);

		MockRestServiceServer server = builder.build();
		server.expect(requestTo("/foo")).andRespond(withSuccess());
		this.restTemplate.getForObject("/foo", Void.class);
		server.verify();

		server = builder.ignoreExpectOrder(true).build();
		server.expect(requestTo("/foo")).andRespond(withSuccess());
		server.expect(requestTo("/bar")).andRespond(withSuccess());
		this.restTemplate.getForObject("/bar", Void.class);
		this.restTemplate.getForObject("/foo", Void.class);
		server.verify();

		server = builder.build();
		server.expect(requestTo("/bar")).andRespond(withSuccess());
		this.restTemplate.getForObject("/bar", Void.class);
		server.verify();
	}

	@Test(expected = AssertionError.class)
	public void exactExpectOrder() {
		MockRestServiceServer server = MockRestServiceServer.bindTo(this.restTemplate)
				.ignoreExpectOrder(false).build();

		server.expect(requestTo("/foo")).andRespond(withSuccess());
		server.expect(requestTo("/bar")).andRespond(withSuccess());
		this.restTemplate.getForObject("/bar", Void.class);
	}

	@Test
	public void ignoreExpectOrder() {
		MockRestServiceServer server = MockRestServiceServer.bindTo(this.restTemplate)
				.ignoreExpectOrder(true).build();

		server.expect(requestTo("/foo")).andRespond(withSuccess());
		server.expect(requestTo("/bar")).andRespond(withSuccess());
		this.restTemplate.getForObject("/bar", Void.class);
		this.restTemplate.getForObject("/foo", Void.class);
		server.verify();
	}

	@Test
	public void resetAndReuseServer() {
		MockRestServiceServer server = MockRestServiceServer.bindTo(this.restTemplate).build();

		server.expect(requestTo("/foo")).andRespond(withSuccess());
		this.restTemplate.getForObject("/foo", Void.class);
		server.verify();
		server.reset();

		server.expect(requestTo("/bar")).andRespond(withSuccess());
		this.restTemplate.getForObject("/bar", Void.class);
		server.verify();
	}

	@Test
	public void resetAndReuseServerWithUnorderedExpectationManager() {
		MockRestServiceServer server = MockRestServiceServer.bindTo(this.restTemplate)
				.ignoreExpectOrder(true).build();

		server.expect(requestTo("/foo")).andRespond(withSuccess());
		this.restTemplate.getForObject("/foo", Void.class);
		server.verify();
		server.reset();

		server.expect(requestTo("/foo")).andRespond(withSuccess());
		server.expect(requestTo("/bar")).andRespond(withSuccess());
		this.restTemplate.getForObject("/bar", Void.class);
		this.restTemplate.getForObject("/foo", Void.class);
		server.verify();
	}

	@Test  // SPR-16132
	public void followUpRequestAfterFailure() {
		MockRestServiceServer server = MockRestServiceServer.bindTo(this.restTemplate).build();

		server.expect(requestTo("/some-service/some-endpoint"))
				.andRespond(request -> { throw new SocketException("pseudo network error"); });

		server.expect(requestTo("/reporting-service/report-error"))
				.andExpect(method(POST)).andRespond(withSuccess());

		try {
			this.restTemplate.getForEntity("/some-service/some-endpoint", String.class);
			fail("Expected exception");
		}
		catch (Exception ex) {
			this.restTemplate.postForEntity("/reporting-service/report-error", ex.toString(), String.class);
		}

		server.verify();
	}

}
