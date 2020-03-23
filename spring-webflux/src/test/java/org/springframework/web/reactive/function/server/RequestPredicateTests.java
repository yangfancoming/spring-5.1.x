

package org.springframework.web.reactive.function.server;

import org.junit.Test;

import static org.junit.Assert.*;


public class RequestPredicateTests {

	@Test
	public void and() {
		RequestPredicate predicate1 = request -> true;
		RequestPredicate predicate2 = request -> true;
		RequestPredicate predicate3 = request -> false;

		MockServerRequest request = MockServerRequest.builder().build();
		assertTrue(predicate1.and(predicate2).test(request));
		assertTrue(predicate2.and(predicate1).test(request));
		assertFalse(predicate1.and(predicate3).test(request));
	}

	@Test
	public void negate() {
		RequestPredicate predicate = request -> false;
		RequestPredicate negated = predicate.negate();

		MockServerRequest mockRequest = MockServerRequest.builder().build();
		assertTrue(negated.test(mockRequest));

		predicate = request -> true;
		negated = predicate.negate();

		assertFalse(negated.test(mockRequest));
	}

	@Test
	public void or() {
		RequestPredicate predicate1 = request -> true;
		RequestPredicate predicate2 = request -> false;
		RequestPredicate predicate3 = request -> false;

		MockServerRequest request = MockServerRequest.builder().build();
		assertTrue(predicate1.or(predicate2).test(request));
		assertTrue(predicate2.or(predicate1).test(request));
		assertFalse(predicate2.or(predicate3).test(request));
	}

}
