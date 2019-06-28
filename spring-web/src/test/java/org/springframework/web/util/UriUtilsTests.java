

package org.springframework.web.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @author Med Belamachi
 */
public class UriUtilsTests {

	private static final Charset CHARSET = StandardCharsets.UTF_8;


	@Test
	public void encodeScheme() {
		assertEquals("Invalid encoded result", "foobar+-.", UriUtils.encodeScheme("foobar+-.", CHARSET));
		assertEquals("Invalid encoded result", "foo%20bar", UriUtils.encodeScheme("foo bar", CHARSET));
	}

	@Test
	public void encodeUserInfo() {
		assertEquals("Invalid encoded result", "foobar:", UriUtils.encodeUserInfo("foobar:", CHARSET));
		assertEquals("Invalid encoded result", "foo%20bar", UriUtils.encodeUserInfo("foo bar", CHARSET));
	}

	@Test
	public void encodeHost() {
		assertEquals("Invalid encoded result", "foobar", UriUtils.encodeHost("foobar", CHARSET));
		assertEquals("Invalid encoded result", "foo%20bar", UriUtils.encodeHost("foo bar", CHARSET));
	}

	@Test
	public void encodePort() {
		assertEquals("Invalid encoded result", "80", UriUtils.encodePort("80", CHARSET));
	}

	@Test
	public void encodePath() {
		assertEquals("Invalid encoded result", "/foo/bar", UriUtils.encodePath("/foo/bar", CHARSET));
		assertEquals("Invalid encoded result", "/foo%20bar", UriUtils.encodePath("/foo bar", CHARSET));
		assertEquals("Invalid encoded result", "/Z%C3%BCrich", UriUtils.encodePath("/Z\u00fcrich", CHARSET));
	}

	@Test
	public void encodePathSegment() {
		assertEquals("Invalid encoded result", "foobar", UriUtils.encodePathSegment("foobar", CHARSET));
		assertEquals("Invalid encoded result", "%2Ffoo%2Fbar", UriUtils.encodePathSegment("/foo/bar", CHARSET));
	}

	@Test
	public void encodeQuery() {
		assertEquals("Invalid encoded result", "foobar", UriUtils.encodeQuery("foobar", CHARSET));
		assertEquals("Invalid encoded result", "foo%20bar", UriUtils.encodeQuery("foo bar", CHARSET));
		assertEquals("Invalid encoded result", "foobar/+", UriUtils.encodeQuery("foobar/+", CHARSET));
		assertEquals("Invalid encoded result", "T%C5%8Dky%C5%8D", UriUtils.encodeQuery("T\u014dky\u014d", CHARSET));
	}

	@Test
	public void encodeQueryParam() {
		assertEquals("Invalid encoded result", "foobar", UriUtils.encodeQueryParam("foobar", CHARSET));
		assertEquals("Invalid encoded result", "foo%20bar", UriUtils.encodeQueryParam("foo bar", CHARSET));
		assertEquals("Invalid encoded result", "foo%26bar", UriUtils.encodeQueryParam("foo&bar", CHARSET));
	}

	@Test
	public void encodeFragment() {
		assertEquals("Invalid encoded result", "foobar", UriUtils.encodeFragment("foobar", CHARSET));
		assertEquals("Invalid encoded result", "foo%20bar", UriUtils.encodeFragment("foo bar", CHARSET));
		assertEquals("Invalid encoded result", "foobar/", UriUtils.encodeFragment("foobar/", CHARSET));
	}

	@Test
	public void encode() {
		assertEquals("Invalid encoded result", "foo", UriUtils.encode("foo", CHARSET));
		assertEquals("Invalid encoded result", "https%3A%2F%2Fexample.com%2Ffoo%20bar",
				UriUtils.encode("https://example.com/foo bar", CHARSET));
	}

	@Test
	public void decode() {
		assertEquals("Invalid encoded URI", "", UriUtils.decode("", CHARSET));
		assertEquals("Invalid encoded URI", "foobar", UriUtils.decode("foobar", CHARSET));
		assertEquals("Invalid encoded URI", "foo bar", UriUtils.decode("foo%20bar", CHARSET));
		assertEquals("Invalid encoded URI", "foo+bar", UriUtils.decode("foo%2bbar", CHARSET));
		assertEquals("Invalid encoded result", "T\u014dky\u014d", UriUtils.decode("T%C5%8Dky%C5%8D", CHARSET));
		assertEquals("Invalid encoded result", "/Z\u00fcrich", UriUtils.decode("/Z%C3%BCrich", CHARSET));
		assertEquals("Invalid encoded result", "T\u014dky\u014d", UriUtils.decode("T\u014dky\u014d", CHARSET));
	}

	@Test(expected = IllegalArgumentException.class)
	public void decodeInvalidSequence() {
		UriUtils.decode("foo%2", CHARSET);
	}

	@Test
	public void extractFileExtension() {
		assertEquals("html", UriUtils.extractFileExtension("index.html"));
		assertEquals("html", UriUtils.extractFileExtension("/index.html"));
		assertEquals("html", UriUtils.extractFileExtension("/products/view.html"));
		assertEquals("html", UriUtils.extractFileExtension("/products/view.html#/a"));
		assertEquals("html", UriUtils.extractFileExtension("/products/view.html#/path/a"));
		assertEquals("html", UriUtils.extractFileExtension("/products/view.html#/path/a.do"));
		assertEquals("html", UriUtils.extractFileExtension("/products/view.html#aaa?bbb"));
		assertEquals("html", UriUtils.extractFileExtension("/products/view.html#aaa.xml?bbb"));
		assertEquals("html", UriUtils.extractFileExtension("/products/view.html?param=a"));
		assertEquals("html", UriUtils.extractFileExtension("/products/view.html?param=/path/a"));
		assertEquals("html", UriUtils.extractFileExtension("/products/view.html?param=/path/a.do"));
		assertEquals("html", UriUtils.extractFileExtension("/products/view.html?param=/path/a#/path/a"));
		assertEquals("html", UriUtils.extractFileExtension("/products/view.html?param=/path/a.do#/path/a.do"));
		assertEquals("html", UriUtils.extractFileExtension("/products;q=11/view.html?param=/path/a.do"));
		assertEquals("html", UriUtils.extractFileExtension("/products;q=11/view.html;r=22?param=/path/a.do"));
		assertEquals("html", UriUtils.extractFileExtension("/products;q=11/view.html;r=22;s=33?param=/path/a.do"));
	}

}
