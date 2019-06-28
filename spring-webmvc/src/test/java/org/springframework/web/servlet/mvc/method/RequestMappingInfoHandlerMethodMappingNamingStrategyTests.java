

package org.springframework.web.servlet.mvc.method;

import java.lang.reflect.Method;

import org.junit.Test;

import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerMethodMappingNamingStrategy;

import static org.junit.Assert.*;

/**
 * Unit tests for
 * {@link org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMethodMappingNamingStrategy}.
 *
 * @author Rossen Stoyanchev
 */
public class RequestMappingInfoHandlerMethodMappingNamingStrategyTests {

	@Test
	public void getNameExplicit() {

		Method method = ClassUtils.getMethod(TestController.class, "handle");
		HandlerMethod handlerMethod = new HandlerMethod(new TestController(), method);

		RequestMappingInfo rmi = new RequestMappingInfo("foo", null, null, null, null, null, null, null);

		HandlerMethodMappingNamingStrategy<RequestMappingInfo> strategy = new RequestMappingInfoHandlerMethodMappingNamingStrategy();

		assertEquals("foo", strategy.getName(handlerMethod, rmi));
	}

	@Test
	public void getNameConvention() {

		Method method = ClassUtils.getMethod(TestController.class, "handle");
		HandlerMethod handlerMethod = new HandlerMethod(new TestController(), method);

		RequestMappingInfo rmi = new RequestMappingInfo(null, null, null, null, null, null, null, null);

		HandlerMethodMappingNamingStrategy<RequestMappingInfo> strategy = new RequestMappingInfoHandlerMethodMappingNamingStrategy();

		assertEquals("TC#handle", strategy.getName(handlerMethod, rmi));
	}


	private static class TestController {

		@RequestMapping
		public void handle() {
		}
	}

}
