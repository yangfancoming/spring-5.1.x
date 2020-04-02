

package org.springframework.web.servlet.handler;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;

import org.springframework.context.support.StaticApplicationContext;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.UrlPathHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


/**
 * Test for {@link AbstractHandlerMethodMapping}.
 */
@SuppressWarnings("unused")
public class HandlerMethodMappingTests {

	private AbstractHandlerMethodMapping<String> mapping;

	private MyHandler handler;

	private Method method1;

	private Method method2;


	@Before
	public void setUp() throws Exception {
		mapping = new MyHandlerMethodMapping();
		handler = new MyHandler();
		method1 = handler.getClass().getMethod("handlerMethod1");
		method2 = handler.getClass().getMethod("handlerMethod2");
	}


	@Test(expected = IllegalStateException.class)
	public void registerDuplicates() {
		mapping.registerMapping("foo", handler, method1);
		mapping.registerMapping("foo", handler, method2);
	}

	@Test
	public void directMatch() throws Exception {
		String key = "foo";
		mapping.registerMapping(key, handler, method1);

		MockHttpServletRequest request = new MockHttpServletRequest("GET", key);
		HandlerMethod result = mapping.getHandlerInternal(request);
		assertEquals(method1, result.getMethod());
		assertEquals(result, request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE));
	}

	@Test
	public void patternMatch() throws Exception {
		mapping.registerMapping("/fo*", handler, method1);
		mapping.registerMapping("/f*", handler, method2);

		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		HandlerMethod result = mapping.getHandlerInternal(request);
		assertEquals(method1, result.getMethod());
		assertEquals(result, request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE));
	}

	@Test(expected = IllegalStateException.class)
	public void ambiguousMatch() throws Exception {
		mapping.registerMapping("/f?o", handler, method1);
		mapping.registerMapping("/fo?", handler, method2);

		mapping.getHandlerInternal(new MockHttpServletRequest("GET", "/foo"));
	}

	@Test
	public void detectHandlerMethodsInAncestorContexts() {
		StaticApplicationContext cxt = new StaticApplicationContext();
		cxt.registerSingleton("myHandler", MyHandler.class);

		AbstractHandlerMethodMapping<String> mapping1 = new MyHandlerMethodMapping();
		mapping1.setApplicationContext(new StaticApplicationContext(cxt));
		mapping1.afterPropertiesSet();

		assertEquals(0, mapping1.getHandlerMethods().size());

		AbstractHandlerMethodMapping<String> mapping2 = new MyHandlerMethodMapping();
		mapping2.setDetectHandlerMethodsInAncestorContexts(true);
		mapping2.setApplicationContext(new StaticApplicationContext(cxt));
		mapping2.afterPropertiesSet();

		assertEquals(2, mapping2.getHandlerMethods().size());
	}

	@Test
	public void registerMapping() throws Exception {

		String key1 = "/foo";
		String key2 = "/foo*";
		mapping.registerMapping(key1, handler, method1);
		mapping.registerMapping(key2, handler, method2);

		// Direct URL lookup

		List<String> directUrlMatches = mapping.getMappingRegistry().getMappingsByUrl(key1);
		assertNotNull(directUrlMatches);
		assertEquals(1, directUrlMatches.size());
		assertEquals(key1, directUrlMatches.get(0));

		// Mapping name lookup

		HandlerMethod handlerMethod1 = new HandlerMethod(handler, method1);
		HandlerMethod handlerMethod2 = new HandlerMethod(handler, method2);

		String name1 = method1.getName();
		List<HandlerMethod> handlerMethods = mapping.getMappingRegistry().getHandlerMethodsByMappingName(name1);
		assertNotNull(handlerMethods);
		assertEquals(1, handlerMethods.size());
		assertEquals(handlerMethod1, handlerMethods.get(0));

		String name2 = method2.getName();
		handlerMethods = mapping.getMappingRegistry().getHandlerMethodsByMappingName(name2);
		assertNotNull(handlerMethods);
		assertEquals(1, handlerMethods.size());
		assertEquals(handlerMethod2, handlerMethods.get(0));

		// CORS lookup
		CorsConfiguration config = mapping.getMappingRegistry().getCorsConfiguration(handlerMethod1);
		assertNotNull(config);
		assertEquals("http://" + handler.hashCode() + name1, config.getAllowedOrigins().get(0));

		config = mapping.getMappingRegistry().getCorsConfiguration(handlerMethod2);
		assertNotNull(config);
		assertEquals("http://" + handler.hashCode() + name2, config.getAllowedOrigins().get(0));
	}

	@Test
	public void registerMappingWithSameMethodAndTwoHandlerInstances() throws Exception {

		String key1 = "foo";
		String key2 = "bar";

		MyHandler handler1 = new MyHandler();
		MyHandler handler2 = new MyHandler();

		HandlerMethod handlerMethod1 = new HandlerMethod(handler1, method1);
		HandlerMethod handlerMethod2 = new HandlerMethod(handler2, method1);

		mapping.registerMapping(key1, handler1, method1);
		mapping.registerMapping(key2, handler2, method1);

		// Direct URL lookup
		List<String> directUrlMatches = mapping.getMappingRegistry().getMappingsByUrl(key1);
		assertNotNull(directUrlMatches);
		assertEquals(1, directUrlMatches.size());
		assertEquals(key1, directUrlMatches.get(0));

		// Mapping name lookup
		String name = method1.getName();
		List<HandlerMethod> handlerMethods = mapping.getMappingRegistry().getHandlerMethodsByMappingName(name);
		assertNotNull(handlerMethods);
		assertEquals(2, handlerMethods.size());
		assertEquals(handlerMethod1, handlerMethods.get(0));
		assertEquals(handlerMethod2, handlerMethods.get(1));

		// CORS lookup
		CorsConfiguration config = mapping.getMappingRegistry().getCorsConfiguration(handlerMethod1);
		assertNotNull(config);
		assertEquals("http://" + handler1.hashCode() + name, config.getAllowedOrigins().get(0));

		config = mapping.getMappingRegistry().getCorsConfiguration(handlerMethod2);
		assertNotNull(config);
		assertEquals("http://" + handler2.hashCode() + name, config.getAllowedOrigins().get(0));
	}

	@Test
	public void unregisterMapping() throws Exception {

		String key = "foo";
		HandlerMethod handlerMethod = new HandlerMethod(handler, method1);

		mapping.registerMapping(key, handler, method1);
		assertNotNull(mapping.getHandlerInternal(new MockHttpServletRequest("GET", key)));

		mapping.unregisterMapping(key);
		assertNull(mapping.getHandlerInternal(new MockHttpServletRequest("GET", key)));
		assertNull(mapping.getMappingRegistry().getMappingsByUrl(key));
		assertNull(mapping.getMappingRegistry().getHandlerMethodsByMappingName(method1.getName()));
		assertNull(mapping.getMappingRegistry().getCorsConfiguration(handlerMethod));
	}

	@Test
	public void getCorsConfigWithBeanNameHandler() throws Exception {

		String key = "foo";
		String beanName = "handler1";

		StaticWebApplicationContext context = new StaticWebApplicationContext();
		context.registerSingleton(beanName, MyHandler.class);

		mapping.setApplicationContext(context);
		mapping.registerMapping(key, beanName, method1);
		HandlerMethod handlerMethod = mapping.getHandlerInternal(new MockHttpServletRequest("GET", key));

		CorsConfiguration config = mapping.getMappingRegistry().getCorsConfiguration(handlerMethod);
		assertNotNull(config);
		assertEquals("http://" + beanName.hashCode() + method1.getName(), config.getAllowedOrigins().get(0));
	}



	private static class MyHandlerMethodMapping extends AbstractHandlerMethodMapping<String> {

		private UrlPathHelper pathHelper = new UrlPathHelper();

		private PathMatcher pathMatcher = new AntPathMatcher();


		public MyHandlerMethodMapping() {
			setHandlerMethodMappingNamingStrategy(new SimpleMappingNamingStrategy());
		}

		@Override
		protected boolean isHandler(Class<?> beanType) {
			return true;
		}

		@Override
		protected String getMappingForMethod(Method method, Class<?> handlerType) {
			String methodName = method.getName();
			return methodName.startsWith("handler") ? methodName : null;
		}

		@Override
		protected Set<String> getMappingPathPatterns(String key) {
			return (pathMatcher.isPattern(key) ? Collections.<String>emptySet() : Collections.singleton(key));
		}

		@Override
		protected CorsConfiguration initCorsConfiguration(Object handler, Method method, String mapping) {
			CorsConfiguration corsConfig = new CorsConfiguration();
			corsConfig.setAllowedOrigins(Collections.singletonList("http://" + handler.hashCode() + method.getName()));
			return corsConfig;
		}

		@Override
		protected String getMatchingMapping(String pattern, HttpServletRequest request) {
			String lookupPath = pathHelper.getLookupPathForRequest(request);
			return pathMatcher.match(pattern, lookupPath) ? pattern : null;
		}

		@Override
		protected Comparator<String> getMappingComparator(HttpServletRequest request) {
			String lookupPath = pathHelper.getLookupPathForRequest(request);
			return pathMatcher.getPatternComparator(lookupPath);
		}

	}

	private static class SimpleMappingNamingStrategy implements HandlerMethodMappingNamingStrategy<String> {
		@Override
		public String getName(HandlerMethod handlerMethod, String mapping) {
			return handlerMethod.getMethod().getName();
		}
	}

	@Controller
	static class MyHandler {

		@RequestMapping
		public void handlerMethod1() {
		}

		@RequestMapping
		public void handlerMethod2() {
		}
	}
}
