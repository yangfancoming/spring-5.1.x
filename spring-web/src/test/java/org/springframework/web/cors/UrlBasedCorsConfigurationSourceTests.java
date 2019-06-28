

package org.springframework.web.cors;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.http.HttpMethod;
import org.springframework.mock.web.test.MockHttpServletRequest;

/**
 * Unit tests for {@link UrlBasedCorsConfigurationSource}.
 * @author Sebastien Deleuze
 */
public class UrlBasedCorsConfigurationSourceTests {

	private final UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();

	@Test
	public void empty() {
		MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.GET.name(), "/bar/test.html");
		assertNull(this.configSource.getCorsConfiguration(request));
	}

	@Test
	public void registerAndMatch() {
		CorsConfiguration config = new CorsConfiguration();
		this.configSource.registerCorsConfiguration("/bar/**", config);

		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo/test.html");
		assertNull(this.configSource.getCorsConfiguration(request));

		request.setRequestURI("/bar/test.html");
		assertEquals(config, this.configSource.getCorsConfiguration(request));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void unmodifiableConfigurationsMap() {
		this.configSource.getCorsConfigurations().put("/**", new CorsConfiguration());
	}

}
