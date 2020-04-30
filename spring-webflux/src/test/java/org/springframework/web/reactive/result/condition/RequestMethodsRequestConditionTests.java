

package org.springframework.web.reactive.result.condition;

import java.net.URISyntaxException;
import java.util.Collections;

import org.junit.Ignore;
import org.junit.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.server.ServerWebExchange;

import static org.junit.Assert.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Unit tests for {@link RequestMethodsRequestCondition}.
 *
 *
 */
public class RequestMethodsRequestConditionTests {

	// TODO: custom method, CORS pre-flight (see @Ignored)

	@Test
	public void getMatchingCondition() throws Exception {
		testMatch(new RequestMethodsRequestCondition(GET), GET);
		testMatch(new RequestMethodsRequestCondition(GET, POST), GET);
		testNoMatch(new RequestMethodsRequestCondition(GET), POST);
	}

	@Test
	public void getMatchingConditionWithHttpHead() throws Exception {
		testMatch(new RequestMethodsRequestCondition(HEAD), HEAD);
		testMatch(new RequestMethodsRequestCondition(GET), GET);
		testNoMatch(new RequestMethodsRequestCondition(POST), HEAD);
	}

	@Test
	public void getMatchingConditionWithEmptyConditions() throws Exception {
		RequestMethodsRequestCondition condition = new RequestMethodsRequestCondition();
		for (RequestMethod method : RequestMethod.values()) {
			if (method != OPTIONS) {
				ServerWebExchange exchange = getExchange(method.name());
				assertNotNull(condition.getMatchingCondition(exchange));
			}
		}
		testNoMatch(condition, OPTIONS);
	}

	@Test
	@Ignore
	public void getMatchingConditionWithCustomMethod() throws Exception {
		ServerWebExchange exchange = getExchange("PROPFIND");
		assertNotNull(new RequestMethodsRequestCondition().getMatchingCondition(exchange));
		assertNull(new RequestMethodsRequestCondition(GET, POST).getMatchingCondition(exchange));
	}

	@Test
	@Ignore
	public void getMatchingConditionWithCorsPreFlight() throws Exception {
		ServerWebExchange exchange = getExchange("OPTIONS");
		exchange.getRequest().getHeaders().add("Origin", "https://example.com");
		exchange.getRequest().getHeaders().add(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "PUT");

		assertNotNull(new RequestMethodsRequestCondition().getMatchingCondition(exchange));
		assertNotNull(new RequestMethodsRequestCondition(PUT).getMatchingCondition(exchange));
		assertNull(new RequestMethodsRequestCondition(DELETE).getMatchingCondition(exchange));
	}

	@Test
	public void compareTo() throws Exception {
		RequestMethodsRequestCondition c1 = new RequestMethodsRequestCondition(GET, HEAD);
		RequestMethodsRequestCondition c2 = new RequestMethodsRequestCondition(POST);
		RequestMethodsRequestCondition c3 = new RequestMethodsRequestCondition();

		ServerWebExchange exchange = getExchange("GET");

		int result = c1.compareTo(c2, exchange);
		assertTrue("Invalid comparison result: " + result, result < 0);

		result = c2.compareTo(c1, exchange);
		assertTrue("Invalid comparison result: " + result, result > 0);

		result = c2.compareTo(c3, exchange);
		assertTrue("Invalid comparison result: " + result, result < 0);

		result = c1.compareTo(c1, exchange);
		assertEquals("Invalid comparison result ", 0, result);
	}

	@Test
	public void combine() {
		RequestMethodsRequestCondition condition1 = new RequestMethodsRequestCondition(GET);
		RequestMethodsRequestCondition condition2 = new RequestMethodsRequestCondition(POST);

		RequestMethodsRequestCondition result = condition1.combine(condition2);
		assertEquals(2, result.getContent().size());
	}


	private void testMatch(RequestMethodsRequestCondition condition, RequestMethod method) throws Exception {
		ServerWebExchange exchange = getExchange(method.name());
		RequestMethodsRequestCondition actual = condition.getMatchingCondition(exchange);
		assertNotNull(actual);
		assertEquals(Collections.singleton(method), actual.getContent());
	}

	private void testNoMatch(RequestMethodsRequestCondition condition, RequestMethod method) throws Exception {
		ServerWebExchange exchange = getExchange(method.name());
		assertNull(condition.getMatchingCondition(exchange));
	}

	private ServerWebExchange getExchange(String method) throws URISyntaxException {
		return MockServerWebExchange.from(MockServerHttpRequest.method(HttpMethod.valueOf(method), "/"));
	}

}
