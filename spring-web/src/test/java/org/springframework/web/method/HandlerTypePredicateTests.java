
package org.springframework.web.method;

import java.util.function.Predicate;

import org.junit.Test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link HandlerTypePredicate}.
 * @author Rossen Stoyanchev
 */
public class HandlerTypePredicateTests {

	@Test
	public void forAnnotation() {

		Predicate<Class<?>> predicate = HandlerTypePredicate.forAnnotation(Controller.class);

		assertTrue(predicate.test(HtmlController.class));
		assertTrue(predicate.test(ApiController.class));
		assertTrue(predicate.test(AnotherApiController.class));
	}

	@Test
	public void forAnnotationWithException() {

		Predicate<Class<?>> predicate = HandlerTypePredicate.forAnnotation(Controller.class)
				.and(HandlerTypePredicate.forAssignableType(Special.class));

		assertFalse(predicate.test(HtmlController.class));
		assertFalse(predicate.test(ApiController.class));
		assertTrue(predicate.test(AnotherApiController.class));
	}


	@Controller
	private static class HtmlController {}

	@RestController
	private static class ApiController {}

	@RestController
	private static class AnotherApiController implements Special {}

	interface Special {}

}
