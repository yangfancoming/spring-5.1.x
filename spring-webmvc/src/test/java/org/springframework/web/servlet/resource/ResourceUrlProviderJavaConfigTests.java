

package org.springframework.web.servlet.resource;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.test.MockFilterChain;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import static org.junit.Assert.*;


/**
 * Integration tests using {@link ResourceUrlEncodingFilter} and
 * {@link ResourceUrlProvider} with the latter configured in Spring MVC Java config.
 *
 *
 */
public class ResourceUrlProviderJavaConfigTests {

	private final TestServlet servlet = new TestServlet();

	private MockFilterChain filterChain;

	private MockHttpServletRequest request;

	private MockHttpServletResponse response;


	@Before
	@SuppressWarnings("resource")
	public void setup() throws Exception {
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.setServletContext(new MockServletContext());
		context.register(WebConfig.class);
		context.refresh();

		this.request = new MockHttpServletRequest("GET", "/");
		this.request.setContextPath("/myapp");
		this.response = new MockHttpServletResponse();

		this.filterChain = new MockFilterChain(this.servlet,
				new ResourceUrlEncodingFilter(),
				(request, response, chain) -> {
					Object urlProvider = context.getBean(ResourceUrlProvider.class);
					request.setAttribute(ResourceUrlProviderExposingInterceptor.RESOURCE_URL_PROVIDER_ATTR, urlProvider);
					chain.doFilter(request, response);
				});
	}

	@Test
	public void resolvePathWithServletMappedAsRoot() throws Exception {
		this.request.setRequestURI("/myapp/index");
		this.request.setServletPath("/index");
		this.filterChain.doFilter(this.request, this.response);

		assertEquals("/myapp/resources/foo-e36d2e05253c6c7085a91522ce43a0b4.css",
				resolvePublicResourceUrlPath("/myapp/resources/foo.css"));
	}

	@Test
	public void resolvePathWithServletMappedByPrefix() throws Exception {
		this.request.setRequestURI("/myapp/myservlet/index");
		this.request.setServletPath("/myservlet");
		this.filterChain.doFilter(this.request, this.response);

		assertEquals("/myapp/myservlet/resources/foo-e36d2e05253c6c7085a91522ce43a0b4.css",
				resolvePublicResourceUrlPath("/myapp/myservlet/resources/foo.css"));
	}

	@Test
	public void resolvePathNoMatch() throws Exception {
		this.request.setRequestURI("/myapp/myservlet/index");
		this.request.setServletPath("/myservlet");
		this.filterChain.doFilter(this.request, this.response);

		assertEquals("/myapp/myservlet/index", resolvePublicResourceUrlPath("/myapp/myservlet/index"));
	}


	private String resolvePublicResourceUrlPath(String path) {
		return this.servlet.wrappedResponse.encodeURL(path);
	}


	@Configuration
	static class WebConfig extends WebMvcConfigurationSupport {

		@Override
		public void addResourceHandlers(ResourceHandlerRegistry registry) {
			registry.addResourceHandler("/resources/**")
				.addResourceLocations("classpath:org/springframework/web/servlet/resource/test/")
				.resourceChain(true).addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));
		}
	}

	@SuppressWarnings("serial")
	private static class TestServlet extends HttpServlet {

		private HttpServletResponse wrappedResponse;

		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response) {
			this.wrappedResponse = response;
		}
	}

}
