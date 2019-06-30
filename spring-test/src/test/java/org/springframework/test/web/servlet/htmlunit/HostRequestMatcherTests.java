

package org.springframework.test.web.servlet.htmlunit;

import org.junit.Test;

/**
 * Unit tests for {@link HostRequestMatcher}.
 *
 * @author Rob Winch
 * @author Sam Brannen
 * @since 4.2
 */
public class HostRequestMatcherTests extends AbstractWebRequestMatcherTests {

	@Test
	public void localhost() throws Exception {
		WebRequestMatcher matcher = new HostRequestMatcher("localhost");
		assertMatches(matcher, "http://localhost/jquery-1.11.0.min.js");
		assertDoesNotMatch(matcher, "http://example.com/jquery-1.11.0.min.js");
	}

	@Test
	public void multipleHosts() throws Exception {
		WebRequestMatcher matcher = new HostRequestMatcher("localhost", "example.com");
		assertMatches(matcher, "http://localhost/jquery-1.11.0.min.js");
		assertMatches(matcher, "http://example.com/jquery-1.11.0.min.js");
	}

	@Test
	public void specificPort() throws Exception {
		WebRequestMatcher matcher = new HostRequestMatcher("localhost:8080");
		assertMatches(matcher, "http://localhost:8080/jquery-1.11.0.min.js");
		assertDoesNotMatch(matcher, "http://localhost:9090/jquery-1.11.0.min.js");
	}

	@Test
	public void defaultHttpPort() throws Exception {
		WebRequestMatcher matcher = new HostRequestMatcher("localhost:80");
		assertMatches(matcher, "http://localhost:80/jquery-1.11.0.min.js");
		assertMatches(matcher, "http://localhost/jquery-1.11.0.min.js");
		assertDoesNotMatch(matcher, "https://localhost/jquery-1.11.0.min.js");
		assertDoesNotMatch(matcher, "http://localhost:9090/jquery-1.11.0.min.js");
	}

	@Test
	public void defaultHttpsPort() throws Exception {
		WebRequestMatcher matcher = new HostRequestMatcher("localhost:443");
		assertMatches(matcher, "https://localhost:443/jquery-1.11.0.min.js");
		assertMatches(matcher, "https://localhost/jquery-1.11.0.min.js");
		assertDoesNotMatch(matcher, "http://localhost/jquery-1.11.0.min.js");
		assertDoesNotMatch(matcher, "https://localhost:9090/jquery-1.11.0.min.js");
	}

}
