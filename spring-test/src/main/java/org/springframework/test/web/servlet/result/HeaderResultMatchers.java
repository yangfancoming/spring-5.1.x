

package org.springframework.test.web.servlet.result;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matcher;

import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.util.AssertionErrors.assertTrue;

/**
 * Factory for response header assertions.
 *
 * <p>An instance of this class is available via
 * {@link MockMvcResultMatchers#header}.
 *
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 * @author Brian Clozel
 * @since 3.2
 */
public class HeaderResultMatchers {

	/**
	 * Protected constructor.
	 * See {@link MockMvcResultMatchers#header()}.
	 */
	protected HeaderResultMatchers() {
	}


	/**
	 * Assert the primary value of the response header with the given Hamcrest
	 * String {@code Matcher}.
	 */
	public ResultMatcher string(final String name, final Matcher<? super String> matcher) {
		return result -> assertThat("Response header '" + name + "'", result.getResponse().getHeader(name), matcher);
	}

	/**
	 * Assert the values of the response header with the given Hamcrest
	 * Iterable {@link Matcher}.
	 * @since 4.3
	 */
	public <T> ResultMatcher stringValues(final String name, final Matcher<Iterable<String>> matcher) {
		return result -> {
			List<String> values = result.getResponse().getHeaders(name);
			assertThat("Response header '" + name + "'", values, matcher);
		};
	}

	/**
	 * Assert the primary value of the response header as a String value.
	 */
	public ResultMatcher string(final String name, final String value) {
		return result -> assertEquals("Response header '" + name + "'", value, result.getResponse().getHeader(name));
	}

	/**
	 * Assert the values of the response header as String values.
	 * @since 4.3
	 */
	public ResultMatcher stringValues(final String name, final String... values) {
		return result -> {
			List<Object> actual = result.getResponse().getHeaderValues(name);
			assertEquals("Response header '" + name + "'", Arrays.asList(values), actual);
		};
	}

	/**
	 * Assert that the named response header exists.
	 * @since 5.0.3
	 */
	public ResultMatcher exists(final String name) {
		return result -> assertTrue("Response should contain header '" + name + "'",
				result.getResponse().containsHeader(name));
	}

	/**
	 * Assert that the named response header does not exist.
	 * @since 4.0
	 */
	public ResultMatcher doesNotExist(final String name) {
		return result -> assertTrue("Response should not contain header '" + name + "'",
				!result.getResponse().containsHeader(name));
	}

	/**
	 * Assert the primary value of the named response header as a {@code long}.
	 * <p>The {@link ResultMatcher} returned by this method throws an
	 * {@link AssertionError} if the response does not contain the specified
	 * header, or if the supplied {@code value} does not match the primary value.
	 */
	public ResultMatcher longValue(final String name, final long value) {
		return result -> {
			MockHttpServletResponse response = result.getResponse();
			assertTrue("Response does not contain header '" + name + "'", response.containsHeader(name));
			String headerValue = response.getHeader(name);
			if (headerValue != null) {
				assertEquals("Response header '" + name + "'", value, Long.parseLong(headerValue));
			}
		};
	}

	/**
	 * Assert the primary value of the named response header parsed into a date
	 * using the preferred date format described in RFC 7231.
	 * <p>The {@link ResultMatcher} returned by this method throws an
	 * {@link AssertionError} if the response does not contain the specified
	 * header, or if the supplied {@code value} does not match the primary value.
	 * @since 4.2
	 * @see <a href="https://tools.ietf.org/html/rfc7231#section-7.1.1.1">Section 7.1.1.1 of RFC 7231</a>
	 */
	public ResultMatcher dateValue(final String name, final long value) {
		return result -> {
			MockHttpServletResponse response = result.getResponse();
			String headerValue = response.getHeader(name);
			assertNotNull("Response does not contain header '" + name + "'", headerValue);

			HttpHeaders headers = new HttpHeaders();
			headers.setDate("expected", value);
			headers.set("actual", headerValue);

			assertEquals("Response header '" + name + "'='" + headerValue + "' " +
							"does not match expected value '" + headers.getFirst("expected") + "'",
					headers.getFirstDate("expected"), headers.getFirstDate("actual"));
		};
	}

}