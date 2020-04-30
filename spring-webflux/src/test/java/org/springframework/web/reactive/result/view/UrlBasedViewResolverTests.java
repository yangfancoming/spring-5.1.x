

package org.springframework.web.reactive.result.view;

import java.time.Duration;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.springframework.context.support.StaticApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link UrlBasedViewResolver}.
 *
 *
 * @author Sebastien Deleuze
 */
public class UrlBasedViewResolverTests {

	private UrlBasedViewResolver resolver;


	@Before
	public void setup() {
		StaticApplicationContext context = new StaticApplicationContext();
		context.refresh();
		this.resolver = new UrlBasedViewResolver();
		this.resolver.setApplicationContext(context);
	}


	@Test
	public void viewNames() throws Exception {
		this.resolver.setViewClass(TestView.class);
		this.resolver.setViewNames("my*");

		Mono<View> mono = this.resolver.resolveViewName("my-view", Locale.US);
		assertNotNull(mono.block());

		mono = this.resolver.resolveViewName("not-my-view", Locale.US);
		assertNull(mono.block());
	}

	@Test
	public void redirectView() throws Exception {
		Mono<View> mono = this.resolver.resolveViewName("redirect:foo", Locale.US);

		StepVerifier.create(mono)
				.consumeNextWith(view -> {
					assertEquals(RedirectView.class, view.getClass());
					RedirectView redirectView = (RedirectView) view;
					assertEquals("foo", redirectView.getUrl());
					assertEquals(HttpStatus.SEE_OTHER, redirectView.getStatusCode());
				})
				.expectComplete()
				.verify(Duration.ZERO);
	}

	@Test
	public void customizedRedirectView() throws Exception {
		this.resolver.setRedirectViewProvider(url -> new RedirectView(url, HttpStatus.FOUND));
		Mono<View> mono = this.resolver.resolveViewName("redirect:foo", Locale.US);

		StepVerifier.create(mono)
				.consumeNextWith(view -> {
					assertEquals(RedirectView.class, view.getClass());
					RedirectView redirectView = (RedirectView) view;
					assertEquals("foo", redirectView.getUrl());
					assertEquals(HttpStatus.FOUND, redirectView.getStatusCode());
				})
				.expectComplete()
				.verify(Duration.ZERO);
	}


	private static class TestView extends AbstractUrlBasedView {

		@Override
		public boolean checkResourceExists(Locale locale) throws Exception {
			return true;
		}

		@Override
		protected Mono<Void> renderInternal(Map<String, Object> attributes, MediaType contentType,
				ServerWebExchange exchange) {

			return Mono.empty();
		}
	}

}
