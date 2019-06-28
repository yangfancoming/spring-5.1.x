

package org.springframework.web.server.i18n;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import static java.util.Locale.*;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link FixedLocaleContextResolver}.
 *
 * @author Sebastien Deleuze
 */
public class FixedLocaleContextResolverTests {

	@Before
	public void setup() {
		Locale.setDefault(US);
	}

	@Test
	public void resolveDefaultLocale() {
		FixedLocaleContextResolver resolver = new FixedLocaleContextResolver();
		assertEquals(US, resolver.resolveLocaleContext(exchange()).getLocale());
		assertEquals(US, resolver.resolveLocaleContext(exchange(CANADA)).getLocale());
	}

	@Test
	public void resolveCustomizedLocale() {
		FixedLocaleContextResolver resolver = new FixedLocaleContextResolver(FRANCE);
		assertEquals(FRANCE, resolver.resolveLocaleContext(exchange()).getLocale());
		assertEquals(FRANCE, resolver.resolveLocaleContext(exchange(CANADA)).getLocale());
	}

	@Test
	public void resolveCustomizedAndTimeZoneLocale() {
		TimeZone timeZone = TimeZone.getTimeZone(ZoneId.of("UTC"));
		FixedLocaleContextResolver resolver = new FixedLocaleContextResolver(FRANCE, timeZone);
		TimeZoneAwareLocaleContext context = (TimeZoneAwareLocaleContext) resolver.resolveLocaleContext(exchange());
		assertEquals(FRANCE, context.getLocale());
		assertEquals(timeZone, context.getTimeZone());
	}

	private ServerWebExchange exchange(Locale... locales) {
		return MockServerWebExchange.from(MockServerHttpRequest.get("").acceptLanguageAsLocales(locales));
	}

}
