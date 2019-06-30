

package org.springframework.test.web.client.samples.matchers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.Person;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * Examples of defining expectations on request headers.
 *
 * @author Rossen Stoyanchev
 */
public class HeaderRequestMatchersIntegrationTests {

	private static final String RESPONSE_BODY = "{\"name\" : \"Ludwig van Beethoven\", \"someDouble\" : \"1.6035\"}";


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
	public void testString() throws Exception {
		this.mockServer.expect(requestTo("/person/1"))
			.andExpect(header("Accept", "application/json, application/*+json"))
			.andRespond(withSuccess(RESPONSE_BODY, MediaType.APPLICATION_JSON));

		executeAndVerify();
	}

	@Test
	public void testStringContains() throws Exception {
		this.mockServer.expect(requestTo("/person/1"))
			.andExpect(header("Accept", containsString("json")))
			.andRespond(withSuccess(RESPONSE_BODY, MediaType.APPLICATION_JSON));

		executeAndVerify();
	}

	private void executeAndVerify() throws URISyntaxException {
		this.restTemplate.getForObject(new URI("/person/1"), Person.class);
		this.mockServer.verify();
	}

}
