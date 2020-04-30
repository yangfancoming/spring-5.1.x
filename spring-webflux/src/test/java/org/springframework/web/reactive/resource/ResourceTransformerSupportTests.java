

package org.springframework.web.reactive.resource;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Mono;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import static org.junit.Assert.*;

/**
 * Unit tests for {@code ResourceTransformerSupport}.
 *
 *
 * @author Brian Clozel
 */
public class ResourceTransformerSupportTests {

	private static final Duration TIMEOUT = Duration.ofSeconds(5);


	private ResourceTransformerChain chain;

	private TestResourceTransformerSupport transformer;


	@Before
	public void setup() {
		VersionResourceResolver versionResolver = new VersionResourceResolver();
		versionResolver.setStrategyMap(Collections.singletonMap("/**", new ContentVersionStrategy()));
		PathResourceResolver pathResolver = new PathResourceResolver();
		pathResolver.setAllowedLocations(new ClassPathResource("test/", getClass()));

		List<ResourceResolver> resolvers = new ArrayList<>();
		resolvers.add(versionResolver);
		resolvers.add(pathResolver);
		ResourceResolverChain resolverChain = new DefaultResourceResolverChain(resolvers);

		this.chain = new DefaultResourceTransformerChain(resolverChain, Collections.emptyList());
		this.transformer = new TestResourceTransformerSupport();
		this.transformer.setResourceUrlProvider(createUrlProvider(resolvers));
	}

	private ResourceUrlProvider createUrlProvider(List<ResourceResolver> resolvers) {
		ResourceWebHandler handler = new ResourceWebHandler();
		handler.setLocations(Collections.singletonList(new ClassPathResource("test/", getClass())));
		handler.setResourceResolvers(resolvers);

		ResourceUrlProvider urlProvider = new ResourceUrlProvider();
		urlProvider.registerHandlers(Collections.singletonMap("/resources/**", handler));
		return urlProvider;
	}


	@Test
	public void resolveUrlPath() {
		MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/resources/main.css"));
		String resourcePath = "/resources/bar.css";
		Resource resource = getResource("main.css");
		String actual = this.transformer.resolveUrlPath(resourcePath, exchange, resource, this.chain).block(TIMEOUT);

		assertEquals("/resources/bar-11e16cf79faee7ac698c805cf28248d2.css", actual);
		assertEquals("/resources/bar-11e16cf79faee7ac698c805cf28248d2.css", actual);
	}

	@Test
	public void resolveUrlPathWithRelativePath() {
		Resource resource = getResource("main.css");
		MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get(""));
		String actual = this.transformer.resolveUrlPath("bar.css", exchange, resource, this.chain).block(TIMEOUT);

		assertEquals("bar-11e16cf79faee7ac698c805cf28248d2.css", actual);
	}

	@Test
	public void resolveUrlPathWithRelativePathInParentDirectory() {
		Resource resource = getResource("images/image.png");
		MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get(""));
		String actual = this.transformer.resolveUrlPath("../bar.css", exchange, resource, this.chain).block(TIMEOUT);

		assertEquals("../bar-11e16cf79faee7ac698c805cf28248d2.css", actual);
	}

	@Test
	public void toAbsolutePath() {
		MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/resources/main.css"));
		String absolute = this.transformer.toAbsolutePath("img/image.png", exchange);
		assertEquals("/resources/img/image.png", absolute);

		absolute = this.transformer.toAbsolutePath("/img/image.png", exchange);
		assertEquals("/img/image.png", absolute);
	}

	private Resource getResource(String filePath) {
		return new ClassPathResource("test/" + filePath, getClass());
	}


	private static class TestResourceTransformerSupport extends ResourceTransformerSupport {

		@Override
		public Mono<Resource> transform(ServerWebExchange ex, Resource res, ResourceTransformerChain chain) {
			return Mono.error(new IllegalStateException("Should never be called"));
		}
	}

}
