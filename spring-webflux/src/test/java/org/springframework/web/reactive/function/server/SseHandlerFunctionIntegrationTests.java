

package org.springframework.web.reactive.function.server;

import java.time.Duration;

import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.Assert.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.web.reactive.function.BodyInserters.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.*;


public class SseHandlerFunctionIntegrationTests extends AbstractRouterFunctionIntegrationTests {

	private WebClient webClient;


	@Before
	public void setup() throws Exception {
		super.setup();
		this.webClient = WebClient.create("http://localhost:" + this.port);
	}

	@Override
	protected RouterFunction<?> routerFunction() {
		SseHandler sseHandler = new SseHandler();
		return route(RequestPredicates.GET("/string"), sseHandler::string)
				.and(route(RequestPredicates.GET("/person"), sseHandler::person))
				.and(route(RequestPredicates.GET("/event"), sseHandler::sse));
	}


	@Test
	public void sseAsString() {
		Flux<String> result = this.webClient.get()
				.uri("/string")
				.accept(TEXT_EVENT_STREAM)
				.retrieve()
				.bodyToFlux(String.class);

		StepVerifier.create(result)
				.expectNext("foo 0")
				.expectNext("foo 1")
				.expectComplete()
				.verify(Duration.ofSeconds(5L));
	}

	@Test
	public void sseAsPerson() {
		Flux<Person> result = this.webClient.get()
				.uri("/person")
				.accept(TEXT_EVENT_STREAM)
				.retrieve()
				.bodyToFlux(Person.class);

		StepVerifier.create(result)
				.expectNext(new Person("foo 0"))
				.expectNext(new Person("foo 1"))
				.expectComplete()
				.verify(Duration.ofSeconds(5L));
	}

	@Test
	public void sseAsEvent() {
		Flux<ServerSentEvent<String>> result = this.webClient.get()
				.uri("/event")
				.accept(TEXT_EVENT_STREAM)
				.retrieve()
				.bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {});

		StepVerifier.create(result)
				.consumeNextWith( event -> {
					assertEquals("0", event.id());
					assertEquals("foo", event.data());
					assertEquals("bar", event.comment());
					assertNull(event.event());
					assertNull(event.retry());
				})
				.consumeNextWith( event -> {
					assertEquals("1", event.id());
					assertEquals("foo", event.data());
					assertEquals("bar", event.comment());
					assertNull(event.event());
					assertNull(event.retry());
				})
				.expectComplete()
				.verify(Duration.ofSeconds(5L));
	}


	private static class SseHandler {

		private static final Flux<Long> INTERVAL = testInterval(Duration.ofMillis(100), 2);

		Mono<ServerResponse> string(ServerRequest request) {
			return ServerResponse.ok()
					.contentType(MediaType.TEXT_EVENT_STREAM)
					.body(INTERVAL.map(aLong -> "foo " + aLong), String.class);
		}

		Mono<ServerResponse> person(ServerRequest request) {
			return ServerResponse.ok()
					.contentType(MediaType.TEXT_EVENT_STREAM)
					.body(INTERVAL.map(aLong -> new Person("foo " + aLong)), Person.class);
		}

		Mono<ServerResponse> sse(ServerRequest request) {
			Flux<ServerSentEvent<String>> body = INTERVAL
					.map(aLong -> ServerSentEvent.builder("foo").id("" + aLong).comment("bar").build());
			return ServerResponse.ok().body(fromServerSentEvents(body));
		}
	}


	@SuppressWarnings("unused")
	private static class Person {

		private String name;

		public Person() {
		}

		public Person(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			Person person = (Person) o;
			return !(this.name != null ? !this.name.equals(person.name) : person.name != null);
		}

		@Override
		public int hashCode() {
			return this.name != null ? this.name.hashCode() : 0;
		}

		@Override
		public String toString() {
			return "Person{" + "name='" + name + '\'' + '}';
		}
	}

}
