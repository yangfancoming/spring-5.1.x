package org.springframework.test.web.client.samples.matchers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.Person;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * Examples of defining expectations on request content and content type.
 *
 *
 * @see JsonPathRequestMatchersIntegrationTests
 * @see XmlContentRequestMatchersIntegrationTests
 * @see XpathRequestMatchersIntegrationTests
 */
public class ContentRequestMatchersIntegrationTests {

	private MockRestServiceServer mockServer;

	private RestTemplate restTemplate;


	@Before
	public void setup() {
		List<HttpMessageConverter<?>> converters = new ArrayList<>();
		converters.add(new StringHttpMessageConverter());
		converters.add(new MappingJackson2HttpMessageConverter());

		this.restTemplate = new RestTemplate();
		this.restTemplate.setMessageConverters(converters);

		this.mockServer = MockRestServiceServer.createServer(this.restTemplate);
	}


	@Test
	public void contentType() throws Exception {
		this.mockServer.expect(content().contentType("application/json;charset=UTF-8")).andRespond(withSuccess());
		executeAndVerify(new Person());
	}

	@Test
	public void contentTypeNoMatch() throws Exception {
		this.mockServer.expect(content().contentType("application/json;charset=UTF-8")).andRespond(withSuccess());
		try {
			executeAndVerify("foo");
		}
		catch (AssertionError error) {
			String message = error.getMessage();
			assertTrue(message, message.startsWith("Content type expected:<application/json;charset=UTF-8>"));
		}
	}

	@Test
	public void contentAsString() throws Exception {
		this.mockServer.expect(content().string("foo")).andRespond(withSuccess());
		executeAndVerify("foo");
	}

	@Test
	public void contentStringStartsWith() throws Exception {
		this.mockServer.expect(content().string(startsWith("foo"))).andRespond(withSuccess());
		executeAndVerify("foo123");
	}

	@Test
	public void contentAsBytes() throws Exception {
		this.mockServer.expect(content().bytes("foo".getBytes())).andRespond(withSuccess());
		executeAndVerify("foo");
	}

	private void executeAndVerify(Object body) throws URISyntaxException {
		this.restTemplate.put(new URI("/foo"), body);
		this.mockServer.verify();
	}

}
