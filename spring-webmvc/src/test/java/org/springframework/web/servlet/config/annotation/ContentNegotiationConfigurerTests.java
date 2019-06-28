

package org.springframework.web.servlet.config.annotation;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import org.springframework.http.MediaType;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.FixedContentNegotiationStrategy;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import static org.junit.Assert.*;

/**
 * Test fixture for {@link ContentNegotiationConfigurer} tests.
 * @author Rossen Stoyanchev
 */
public class ContentNegotiationConfigurerTests {

	private ContentNegotiationConfigurer configurer;

	private NativeWebRequest webRequest;

	private MockHttpServletRequest servletRequest;


	@Before
	public void setup() {
		this.servletRequest = new MockHttpServletRequest();
		this.webRequest = new ServletWebRequest(this.servletRequest);
		this.configurer = new ContentNegotiationConfigurer(this.servletRequest.getServletContext());
	}


	@Test
	public void defaultSettings() throws Exception {
		ContentNegotiationManager manager = this.configurer.buildContentNegotiationManager();

		this.servletRequest.setRequestURI("/flower.gif");

		assertEquals("Should be able to resolve file extensions by default",
				MediaType.IMAGE_GIF, manager.resolveMediaTypes(this.webRequest).get(0));

		this.servletRequest.setRequestURI("/flower?format=gif");
		this.servletRequest.addParameter("format", "gif");

		assertEquals("Should not resolve request parameters by default",
				ContentNegotiationStrategy.MEDIA_TYPE_ALL_LIST, manager.resolveMediaTypes(this.webRequest));

		this.servletRequest.setRequestURI("/flower");
		this.servletRequest.addHeader("Accept", MediaType.IMAGE_GIF_VALUE);

		assertEquals("Should resolve Accept header by default",
				MediaType.IMAGE_GIF, manager.resolveMediaTypes(this.webRequest).get(0));
	}

	@Test
	public void addMediaTypes() throws Exception {
		this.configurer.mediaTypes(Collections.singletonMap("json", MediaType.APPLICATION_JSON));
		ContentNegotiationManager manager = this.configurer.buildContentNegotiationManager();

		this.servletRequest.setRequestURI("/flower.json");
		assertEquals(MediaType.APPLICATION_JSON, manager.resolveMediaTypes(this.webRequest).get(0));
	}

	@Test
	public void favorParameter() throws Exception {
		this.configurer.favorParameter(true);
		this.configurer.parameterName("f");
		this.configurer.mediaTypes(Collections.singletonMap("json", MediaType.APPLICATION_JSON));
		ContentNegotiationManager manager = this.configurer.buildContentNegotiationManager();

		this.servletRequest.setRequestURI("/flower");
		this.servletRequest.addParameter("f", "json");

		assertEquals(MediaType.APPLICATION_JSON, manager.resolveMediaTypes(this.webRequest).get(0));
	}

	@Test
	public void ignoreAcceptHeader() throws Exception {
		this.configurer.ignoreAcceptHeader(true);
		ContentNegotiationManager manager = this.configurer.buildContentNegotiationManager();

		this.servletRequest.setRequestURI("/flower");
		this.servletRequest.addHeader("Accept", MediaType.IMAGE_GIF_VALUE);

		assertEquals(ContentNegotiationStrategy.MEDIA_TYPE_ALL_LIST, manager.resolveMediaTypes(this.webRequest));
	}

	@Test
	public void setDefaultContentType() throws Exception {
		this.configurer.defaultContentType(MediaType.APPLICATION_JSON);
		ContentNegotiationManager manager = this.configurer.buildContentNegotiationManager();

		assertEquals(MediaType.APPLICATION_JSON, manager.resolveMediaTypes(this.webRequest).get(0));
	}

	@Test
	public void setMultipleDefaultContentTypes() throws Exception {
		this.configurer.defaultContentType(MediaType.APPLICATION_JSON, MediaType.ALL);
		ContentNegotiationManager manager = this.configurer.buildContentNegotiationManager();

		assertEquals(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.ALL), manager.resolveMediaTypes(this.webRequest));
	}

	@Test
	public void setDefaultContentTypeStrategy() throws Exception {
		this.configurer.defaultContentTypeStrategy(new FixedContentNegotiationStrategy(MediaType.APPLICATION_JSON));
		ContentNegotiationManager manager = this.configurer.buildContentNegotiationManager();

		assertEquals(MediaType.APPLICATION_JSON, manager.resolveMediaTypes(this.webRequest).get(0));
	}

}
