

package org.springframework.test.web.reactive.server.samples.bind;

import org.junit.Before;
import org.junit.Test;

import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * Sample tests demonstrating "mock" server tests binding to a RouterFunction.
 *
 * @author Rossen Stoyanchev
 * @since 5.0
 */
public class RouterFunctionTests {

	private WebTestClient testClient;


	@Before
	public void setUp() throws Exception {

		RouterFunction<?> route = route(GET("/test"), request ->
				ServerResponse.ok().syncBody("It works!"));

		this.testClient = WebTestClient.bindToRouterFunction(route).build();
	}

	@Test
	public void test() throws Exception {
		this.testClient.get().uri("/test")
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class).isEqualTo("It works!");
	}

}
