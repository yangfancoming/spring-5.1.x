

package org.springframework.test.web.servlet.htmlunit;

import java.net.MalformedURLException;
import java.net.URL;

import com.gargoylesoftware.htmlunit.WebRequest;

import static org.junit.Assert.*;

/**
 * Abstract base class for testing {@link WebRequestMatcher} implementations.
 *
 * @author Sam Brannen
 * @since 4.2
 */
public class AbstractWebRequestMatcherTests {

	protected void assertMatches(WebRequestMatcher matcher, String url) throws MalformedURLException {
		assertTrue(matcher.matches(new WebRequest(new URL(url))));
	}

	protected void assertDoesNotMatch(WebRequestMatcher matcher, String url) throws MalformedURLException {
		assertFalse(matcher.matches(new WebRequest(new URL(url))));
	}

}
