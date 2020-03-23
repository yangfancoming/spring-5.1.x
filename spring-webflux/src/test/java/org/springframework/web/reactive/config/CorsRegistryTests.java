

package org.springframework.web.reactive.config;

import java.util.Arrays;
import java.util.Map;

import org.junit.Test;

import org.springframework.web.cors.CorsConfiguration;

import static org.junit.Assert.*;

/**
 * Test fixture with a {@link CorsRegistry}.
 *
 * @author Sebastien Deleuze
 */
public class CorsRegistryTests {

	private final CorsRegistry registry = new CorsRegistry();


	@Test
	public void noMapping() {
		assertTrue(this.registry.getCorsConfigurations().isEmpty());
	}

	@Test
	public void multipleMappings() {
		this.registry.addMapping("/foo");
		this.registry.addMapping("/bar");
		assertEquals(2, this.registry.getCorsConfigurations().size());
	}

	@Test
	public void customizedMapping() {
		this.registry.addMapping("/foo").allowedOrigins("https://domain2.com", "https://domain2.com")
				.allowedMethods("DELETE").allowCredentials(false).allowedHeaders("header1", "header2")
				.exposedHeaders("header3", "header4").maxAge(3600);
		Map<String, CorsConfiguration> configs = this.registry.getCorsConfigurations();
		assertEquals(1, configs.size());
		CorsConfiguration config = configs.get("/foo");
		assertEquals(Arrays.asList("https://domain2.com", "https://domain2.com"), config.getAllowedOrigins());
		assertEquals(Arrays.asList("DELETE"), config.getAllowedMethods());
		assertEquals(Arrays.asList("header1", "header2"), config.getAllowedHeaders());
		assertEquals(Arrays.asList("header3", "header4"), config.getExposedHeaders());
		assertEquals(false, config.getAllowCredentials());
		assertEquals(Long.valueOf(3600), config.getMaxAge());
	}

}
