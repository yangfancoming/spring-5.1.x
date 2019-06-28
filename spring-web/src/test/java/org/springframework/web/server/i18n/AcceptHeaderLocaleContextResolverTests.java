

package org.springframework.web.server.i18n;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import org.junit.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import static java.util.Locale.*;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link AcceptHeaderLocaleContextResolver}.
 *
 * @author Sebastien Deleuze
 * @author Juergen Hoeller
 */
public class AcceptHeaderLocaleContextResolverTests {

	private final AcceptHeaderLocaleContextResolver resolver = new AcceptHeaderLocaleContextResolver();


	@Test
	public void resolve() {
		assertEquals(CANADA, this.resolver.resolveLocaleContext(exchange(CANADA)).getLocale());
		assertEquals(US, this.resolver.resolveLocaleContext(exchange(US, CANADA)).getLocale());
	}

	@Test
	public void resolvePreferredSupported() {
		this.resolver.setSupportedLocales(Collections.singletonList(CANADA));
		assertEquals(CANADA, this.resolver.resolveLocaleContext(exchange(US, CANADA)).getLocale());
	}

	@Test
	public void resolvePreferredNotSupported() {
		this.resolver.setSupportedLocales(Collections.singletonList(CANADA));
		assertEquals(US, this.resolver.resolveLocaleContext(exchange(US, UK)).getLocale());
	}

	@Test
	public void resolvePreferredNotSupportedWithDefault() {
		this.resolver.setSupportedLocales(Arrays.asList(US, JAPAN));
		this.resolver.setDefaultLocale(JAPAN);
		assertEquals(JAPAN, this.resolver.resolveLocaleContext(exchange(KOREA)).getLocale());
	}

	@Test
	public void resolvePreferredAgainstLanguageOnly() {
		this.resolver.setSupportedLocales(Collections.singletonList(ENGLISH));
		assertEquals(ENGLISH, this.resolver.resolveLocaleContext(exchange(GERMANY, US, UK)).getLocale());
	}

	@Test
	public void resolvePreferredAgainstCountryIfPossible() {
		this.resolver.setSupportedLocales(Arrays.asList(ENGLISH, UK));
		assertEquals(UK, this.resolver.resolveLocaleContext(exchange(GERMANY, US, UK)).getLocale());
	}

	@Test
	public void resolvePreferredAgainstLanguageWithMultipleSupportedLocales() {
		this.resolver.setSupportedLocales(Arrays.asList(GERMAN, US));
		assertEquals(GERMAN, this.resolver.resolveLocaleContext(exchange(GERMANY, US, UK)).getLocale());
	}

	@Test
	public void resolveMissingAcceptLanguageHeader() {
		MockServerHttpRequest request = MockServerHttpRequest.get("/").build();
		MockServerWebExchange exchange = MockServerWebExchange.from(request);
		assertNull(this.resolver.resolveLocaleContext(exchange).getLocale());
	}

	@Test
	public void resolveMissingAcceptLanguageHeaderWithDefault() {
		this.resolver.setDefaultLocale(US);

		MockServerHttpRequest request = MockServerHttpRequest.get("/").build();
		MockServerWebExchange exchange = MockServerWebExchange.from(request);
		assertEquals(US, this.resolver.resolveLocaleContext(exchange).getLocale());
	}

	@Test
	public void resolveEmptyAcceptLanguageHeader() {
		MockServerHttpRequest request = MockServerHttpRequest.get("/").header(HttpHeaders.ACCEPT_LANGUAGE, "").build();
		MockServerWebExchange exchange = MockServerWebExchange.from(request);
		assertNull(this.resolver.resolveLocaleContext(exchange).getLocale());
	}

	@Test
	public void resolveEmptyAcceptLanguageHeaderWithDefault() {
		this.resolver.setDefaultLocale(US);

		MockServerHttpRequest request = MockServerHttpRequest.get("/").header(HttpHeaders.ACCEPT_LANGUAGE, "").build();
		MockServerWebExchange exchange = MockServerWebExchange.from(request);
		assertEquals(US, this.resolver.resolveLocaleContext(exchange).getLocale());
	}

	@Test
	public void resolveInvalidAcceptLanguageHeader() {
		MockServerHttpRequest request = MockServerHttpRequest.get("/").header(HttpHeaders.ACCEPT_LANGUAGE, "en_US").build();
		MockServerWebExchange exchange = MockServerWebExchange.from(request);
		assertNull(this.resolver.resolveLocaleContext(exchange).getLocale());
	}

	@Test
	public void resolveInvalidAcceptLanguageHeaderWithDefault() {
		this.resolver.setDefaultLocale(US);

		MockServerHttpRequest request = MockServerHttpRequest.get("/").header(HttpHeaders.ACCEPT_LANGUAGE, "en_US").build();
		MockServerWebExchange exchange = MockServerWebExchange.from(request);
		assertEquals(US, this.resolver.resolveLocaleContext(exchange).getLocale());
	}

	@Test
	public void defaultLocale() {
		this.resolver.setDefaultLocale(JAPANESE);
		MockServerHttpRequest request = MockServerHttpRequest.get("/").build();
		MockServerWebExchange exchange = MockServerWebExchange.from(request);
		assertEquals(JAPANESE, this.resolver.resolveLocaleContext(exchange).getLocale());

		request = MockServerHttpRequest.get("/").acceptLanguageAsLocales(US).build();
		exchange = MockServerWebExchange.from(request);
		assertEquals(US, this.resolver.resolveLocaleContext(exchange).getLocale());
	}


	private ServerWebExchange exchange(Locale... locales) {
		return MockServerWebExchange.from(MockServerHttpRequest.get("").acceptLanguageAsLocales(locales));
	}

}
