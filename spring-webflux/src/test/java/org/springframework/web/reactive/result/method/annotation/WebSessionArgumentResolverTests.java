
package org.springframework.web.reactive.result.method.annotation;

import io.reactivex.Single;
import org.junit.Test;
import reactor.core.publisher.Mono;

import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.web.method.ResolvableMethod;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link WebSessionArgumentResolver}.
 * @author Rossen Stoyanchev
 */
public class WebSessionArgumentResolverTests {

	private final WebSessionArgumentResolver resolver =
			new WebSessionArgumentResolver(ReactiveAdapterRegistry.getSharedInstance());

	private ResolvableMethod testMethod = ResolvableMethod.on(getClass()).named("handle").build();


	@Test
	public void supportsParameter() {
		assertTrue(this.resolver.supportsParameter(this.testMethod.arg(WebSession.class)));
		assertTrue(this.resolver.supportsParameter(this.testMethod.arg(Mono.class, WebSession.class)));
		assertTrue(this.resolver.supportsParameter(this.testMethod.arg(Single.class, WebSession.class)));
	}


	@Test
	public void resolverArgument() {

		BindingContext context = new BindingContext();
		WebSession session = mock(WebSession.class);
		MockServerHttpRequest request = MockServerHttpRequest.get("/").build();
		ServerWebExchange exchange = MockServerWebExchange.builder(request).session(session).build();

		MethodParameter param = this.testMethod.arg(WebSession.class);
		Object actual = this.resolver.resolveArgument(param, context, exchange).block();
		assertSame(session, actual);

		param = this.testMethod.arg(Mono.class, WebSession.class);
		actual = this.resolver.resolveArgument(param, context, exchange).block();
		assertNotNull(actual);
		assertTrue(Mono.class.isAssignableFrom(actual.getClass()));
		assertSame(session, ((Mono<?>) actual).block());

		param = this.testMethod.arg(Single.class, WebSession.class);
		actual = this.resolver.resolveArgument(param, context, exchange).block();
		assertNotNull(actual);
		assertTrue(Single.class.isAssignableFrom(actual.getClass()));
		assertSame(session, ((Single<?>) actual).blockingGet());
	}


	@SuppressWarnings("unused")
	void handle(
			WebSession user,
			Mono<WebSession> userMono,
			Single<WebSession> singleUser) {
	}

}
