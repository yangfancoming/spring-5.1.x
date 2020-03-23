

package org.springframework.web.reactive.function.server;

import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.Assert.*;
import static org.springframework.web.reactive.function.BodyInserters.*;


@SuppressWarnings("unchecked")
public class RouterFunctionTests {

	@Test
	public void and() {
		HandlerFunction<ServerResponse> handlerFunction = request -> ServerResponse.ok().build();
		RouterFunction<ServerResponse> routerFunction1 = request -> Mono.empty();
		RouterFunction<ServerResponse> routerFunction2 = request -> Mono.just(handlerFunction);

		RouterFunction<ServerResponse> result = routerFunction1.and(routerFunction2);
		assertNotNull(result);

		MockServerRequest request = MockServerRequest.builder().build();
		Mono<HandlerFunction<ServerResponse>> resultHandlerFunction = result.route(request);

		StepVerifier.create(resultHandlerFunction)
				.expectNext(handlerFunction)
				.expectComplete()
				.verify();
	}

	@Test
	public void andOther() {
		HandlerFunction<ServerResponse> handlerFunction =
				request -> ServerResponse.ok().body(fromObject("42"));
		RouterFunction<?> routerFunction1 = request -> Mono.empty();
		RouterFunction<ServerResponse> routerFunction2 =
				request -> Mono.just(handlerFunction);

		RouterFunction<?> result = routerFunction1.andOther(routerFunction2);
		assertNotNull(result);

		MockServerRequest request = MockServerRequest.builder().build();
		Mono<? extends HandlerFunction<?>> resultHandlerFunction = result.route(request);

		StepVerifier.create(resultHandlerFunction)
				.expectNextMatches(o -> o.equals(handlerFunction))
				.expectComplete()
				.verify();
	}

	@Test
	public void andRoute() {
		RouterFunction<ServerResponse> routerFunction1 = request -> Mono.empty();
		RequestPredicate requestPredicate = request -> true;

		RouterFunction<ServerResponse> result = routerFunction1.andRoute(requestPredicate, this::handlerMethod);
		assertNotNull(result);

		MockServerRequest request = MockServerRequest.builder().build();
		Mono<? extends HandlerFunction<?>> resultHandlerFunction = result.route(request);

		StepVerifier.create(resultHandlerFunction)
				.expectNextCount(1)
				.expectComplete()
				.verify();
	}

	@Test
	public void filter() {
		Mono<String> stringMono = Mono.just("42");
		HandlerFunction<EntityResponse<Mono<String>>> handlerFunction =
				request -> EntityResponse.fromPublisher(stringMono, String.class).build();
		RouterFunction<EntityResponse<Mono<String>>> routerFunction =
				request -> Mono.just(handlerFunction);

		HandlerFilterFunction<EntityResponse<Mono<String>>, EntityResponse<Mono<Integer>>> filterFunction =
				(request, next) -> next.handle(request).flatMap(
						response -> {
							Mono<Integer> intMono = response.entity()
									.map(Integer::parseInt);
							return EntityResponse.fromPublisher(intMono, Integer.class).build();
						});

		RouterFunction<EntityResponse<Mono<Integer>>> result = routerFunction.filter(filterFunction);
		assertNotNull(result);

		MockServerRequest request = MockServerRequest.builder().build();
		Mono<EntityResponse<Mono<Integer>>> responseMono =
				result.route(request).flatMap(hf -> hf.handle(request));

		StepVerifier.create(responseMono)
				.consumeNextWith(
						serverResponse -> {
							StepVerifier.create(serverResponse.entity())
									.expectNext(42)
									.expectComplete()
									.verify();
						})
				.expectComplete()
				.verify();
	}


	private Mono<ServerResponse> handlerMethod(ServerRequest request) {
		return ServerResponse.ok().body(fromObject("42"));
	}

}
