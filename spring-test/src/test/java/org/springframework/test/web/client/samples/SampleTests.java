
package org.springframework.test.web.client.samples;

import java.io.IOException;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.web.Person;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.*;
import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.ExpectedCount.never;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Examples to demonstrate writing client-side REST tests with Spring MVC Test.
 * While the tests in this class invoke the RestTemplate directly, in actual
 * tests the RestTemplate may likely be invoked indirectly, i.e. through client
 * code.
 *
 *
 */
public class SampleTests {

	private MockRestServiceServer mockServer;
	private RestTemplate restTemplate;
	private String responseBody = "{\"name\" : \"Ludwig van Beethoven\", \"someDouble\" : \"1.6035\"}";

	@Before
	public void setup() {
		this.restTemplate = new RestTemplate();
		this.mockServer = MockRestServiceServer.bindTo(this.restTemplate).ignoreExpectOrder(true).build();
	}

	@Test
	public void performGet() {
		this.mockServer.expect(
				requestTo("/composers/42"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

		@SuppressWarnings("unused")
		Person ludwig = this.restTemplate.getForObject("/composers/{id}", Person.class, 42);
		// We are only validating the request. The response is mocked out.
		// hotel.getId() == 42
		// hotel.getName().equals("Holiday Inn")
		this.mockServer.verify();
	}

	@Test
	public void performGetManyTimes() {

		this.mockServer.expect(
				manyTimes(),
				requestTo("/composers/42"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

		@SuppressWarnings("unused")
		Person ludwig = this.restTemplate.getForObject("/composers/{id}", Person.class, 42);

		// We are only validating the request. The response is mocked out.
		// hotel.getId() == 42
		// hotel.getName().equals("Holiday Inn")

		this.restTemplate.getForObject("/composers/{id}", Person.class, 42);
		this.restTemplate.getForObject("/composers/{id}", Person.class, 42);
		this.restTemplate.getForObject("/composers/{id}", Person.class, 42);

		this.mockServer.verify();
	}

	@Test
	public void expectNever() {
		this.mockServer.expect(once(), requestTo("/composers/42")).andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));
		this.mockServer.expect(never(), requestTo("/composers/43")).andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));
		this.restTemplate.getForObject("/composers/{id}", Person.class, 42);
		this.mockServer.verify();
	}

	@Test(expected = AssertionError.class)
	public void expectNeverViolated() {
		this.mockServer.expect(once(), requestTo("/composers/42")).andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));
		this.mockServer.expect(never(), requestTo("/composers/43")).andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));
		this.restTemplate.getForObject("/composers/{id}", Person.class, 42);
		this.restTemplate.getForObject("/composers/{id}", Person.class, 43);
	}

	@Test
	public void performGetWithResponseBodyFromFile() {

		Resource responseBody = new ClassPathResource("ludwig.json", this.getClass());
		this.mockServer.expect(requestTo("/composers/42")).andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

		@SuppressWarnings("unused")
		Person ludwig = this.restTemplate.getForObject("/composers/{id}", Person.class, 42);
		// hotel.getId() == 42
		// hotel.getName().equals("Holiday Inn")
		this.mockServer.verify();
	}

	@Test
	public void verify() {

		this.mockServer.expect(requestTo("/number")).andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess("1", MediaType.TEXT_PLAIN));

		this.mockServer.expect(requestTo("/number")).andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess("2", MediaType.TEXT_PLAIN));

		this.mockServer.expect(requestTo("/number")).andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess("4", MediaType.TEXT_PLAIN));

		this.mockServer.expect(requestTo("/number")).andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess("8", MediaType.TEXT_PLAIN));

		@SuppressWarnings("unused")
		String result1 = this.restTemplate.getForObject("/number", String.class);
		// result1 == "1"

		@SuppressWarnings("unused")
		String result2 = this.restTemplate.getForObject("/number", String.class);
		// result == "2"

		try {
			this.mockServer.verify();
		}
		catch (AssertionError error) {
			assertTrue(error.getMessage(), error.getMessage().contains("2 unsatisfied expectation(s)"));
		}
	}

	@Test // SPR-14694
	public void repeatedAccessToResponseViaResource() {

		Resource resource = new ClassPathResource("ludwig.json", this.getClass());

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setInterceptors(Collections.singletonList(new ContentInterceptor(resource)));

		MockRestServiceServer mockServer = MockRestServiceServer.bindTo(restTemplate)
				.ignoreExpectOrder(true)
				.bufferContent()  // enable repeated reads of response body
				.build();

		mockServer.expect(requestTo("/composers/42")).andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(resource, MediaType.APPLICATION_JSON));

		restTemplate.getForObject("/composers/{id}", Person.class, 42);
		mockServer.verify();
	}


}
