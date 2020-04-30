

package org.springframework.http;

import java.time.Duration;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link ResponseCookie}.
 *
 */
public class ResponseCookieTests {

	@Test
	public void defaultValues() {
		assertEquals("id=1fWa", ResponseCookie.from("id", "1fWa").build().toString());
	}

	@Test
	public void httpOnlyStrictSecureWithDomainAndPath() {
		assertEquals("id=1fWa; Path=/projects; Domain=spring.io; Secure; HttpOnly; SameSite=strict",
				ResponseCookie.from("id", "1fWa").domain("spring.io").path("/projects")
						.httpOnly(true).secure(true).sameSite("strict").build().toString());
	}

	@Test
	public void maxAge() {

		Duration maxAge = Duration.ofDays(365);
		String expires = HttpHeaders.formatDate(System.currentTimeMillis() + maxAge.toMillis());
		expires = expires.substring(0, expires.indexOf(":") + 1);

		assertThat(ResponseCookie.from("id", "1fWa").maxAge(maxAge).build().toString(), allOf(
				startsWith("id=1fWa; Max-Age=31536000; Expires=" + expires),
				endsWith(" GMT")));

		assertThat(ResponseCookie.from("id", "1fWa").maxAge(maxAge.getSeconds()).build().toString(), allOf(
				startsWith("id=1fWa; Max-Age=31536000; Expires=" + expires),
				endsWith(" GMT")));
	}

	@Test
	public void maxAge0() {
		assertEquals("id=1fWa; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT",
				ResponseCookie.from("id", "1fWa").maxAge(Duration.ofSeconds(0)).build().toString());

		assertEquals("id=1fWa; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT",
				ResponseCookie.from("id", "1fWa").maxAge(0).build().toString());
	}

}
