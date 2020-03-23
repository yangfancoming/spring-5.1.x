

package org.springframework.web.reactive.result.method.annotation;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.reactive.BindingContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit tests for {@link ExpressionValueMethodArgumentResolver}.
 *
 * @author Rossen Stoyanchev
 */
public class ExpressionValueMethodArgumentResolverTests {

	private ExpressionValueMethodArgumentResolver resolver;

	private final MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));

	private MethodParameter paramSystemProperty;
	private MethodParameter paramNotSupported;
	private MethodParameter paramAlsoNotSupported;


	@Before
	@SuppressWarnings("resource")
	public void setup() throws Exception {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.refresh();
		ReactiveAdapterRegistry adapterRegistry = ReactiveAdapterRegistry.getSharedInstance();
		this.resolver = new ExpressionValueMethodArgumentResolver(context.getBeanFactory(), adapterRegistry);

		Method method = ReflectionUtils.findMethod(getClass(), "params", (Class<?>[]) null);
		this.paramSystemProperty = new MethodParameter(method, 0);
		this.paramNotSupported = new MethodParameter(method, 1);
		this.paramAlsoNotSupported = new MethodParameter(method, 2);
	}


	@Test
	public void supportsParameter() throws Exception {
		assertTrue(this.resolver.supportsParameter(this.paramSystemProperty));
	}

	@Test
	public void doesNotSupport() throws Exception {
		assertFalse(this.resolver.supportsParameter(this.paramNotSupported));
		try {
			this.resolver.supportsParameter(this.paramAlsoNotSupported);
			fail();
		}
		catch (IllegalStateException ex) {
			assertTrue("Unexpected error message:\n" + ex.getMessage(),
					ex.getMessage().startsWith(
							"ExpressionValueMethodArgumentResolver doesn't support reactive type wrapper"));
		}
	}

	@Test
	public void resolveSystemProperty() throws Exception {
		System.setProperty("systemProperty", "22");
		try {
			Mono<Object> mono = this.resolver.resolveArgument(
					this.paramSystemProperty,  new BindingContext(), this.exchange);

			Object value = mono.block();
			assertEquals(22, value);
		}
		finally {
			System.clearProperty("systemProperty");
		}

	}

	// TODO: test with expression for ServerWebExchange


	@SuppressWarnings("unused")
	public void params(
			@Value("#{systemProperties.systemProperty}") int param1,
			String notSupported,
			@Value("#{systemProperties.foo}") Mono<String> alsoNotSupported) {
	}

}
