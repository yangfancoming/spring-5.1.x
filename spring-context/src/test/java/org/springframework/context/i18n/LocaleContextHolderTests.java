

package org.springframework.context.i18n;

import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Juergen Hoeller
 */
public class LocaleContextHolderTests {

	@Test
	public void testSetLocaleContext() {
		LocaleContext lc = new SimpleLocaleContext(Locale.GERMAN);
		LocaleContextHolder.setLocaleContext(lc);
		assertSame(lc, LocaleContextHolder.getLocaleContext());
		assertEquals(Locale.GERMAN, LocaleContextHolder.getLocale());
		assertEquals(TimeZone.getDefault(), LocaleContextHolder.getTimeZone());

		lc = new SimpleLocaleContext(Locale.GERMANY);
		LocaleContextHolder.setLocaleContext(lc);
		assertSame(lc, LocaleContextHolder.getLocaleContext());
		assertEquals(Locale.GERMANY, LocaleContextHolder.getLocale());
		assertEquals(TimeZone.getDefault(), LocaleContextHolder.getTimeZone());

		LocaleContextHolder.resetLocaleContext();
		assertNull(LocaleContextHolder.getLocaleContext());
		assertEquals(Locale.getDefault(), LocaleContextHolder.getLocale());
		assertEquals(TimeZone.getDefault(), LocaleContextHolder.getTimeZone());
	}

	@Test
	public void testSetTimeZoneAwareLocaleContext() {
		LocaleContext lc = new SimpleTimeZoneAwareLocaleContext(Locale.GERMANY, TimeZone.getTimeZone("GMT+1"));
		LocaleContextHolder.setLocaleContext(lc);
		assertSame(lc, LocaleContextHolder.getLocaleContext());
		assertEquals(Locale.GERMANY, LocaleContextHolder.getLocale());
		assertEquals(TimeZone.getTimeZone("GMT+1"), LocaleContextHolder.getTimeZone());

		LocaleContextHolder.resetLocaleContext();
		assertNull(LocaleContextHolder.getLocaleContext());
		assertEquals(Locale.getDefault(), LocaleContextHolder.getLocale());
		assertEquals(TimeZone.getDefault(), LocaleContextHolder.getTimeZone());
	}

	@Test
	public void testSetLocale() {
		LocaleContextHolder.setLocale(Locale.GERMAN);
		assertEquals(Locale.GERMAN, LocaleContextHolder.getLocale());
		assertEquals(TimeZone.getDefault(), LocaleContextHolder.getTimeZone());
		assertFalse(LocaleContextHolder.getLocaleContext() instanceof TimeZoneAwareLocaleContext);
		assertEquals(Locale.GERMAN, LocaleContextHolder.getLocaleContext().getLocale());

		LocaleContextHolder.setLocale(Locale.GERMANY);
		assertEquals(Locale.GERMANY, LocaleContextHolder.getLocale());
		assertEquals(TimeZone.getDefault(), LocaleContextHolder.getTimeZone());
		assertFalse(LocaleContextHolder.getLocaleContext() instanceof TimeZoneAwareLocaleContext);
		assertEquals(Locale.GERMANY, LocaleContextHolder.getLocaleContext().getLocale());

		LocaleContextHolder.setLocale(null);
		assertNull(LocaleContextHolder.getLocaleContext());
		assertEquals(Locale.getDefault(), LocaleContextHolder.getLocale());
		assertEquals(TimeZone.getDefault(), LocaleContextHolder.getTimeZone());

		LocaleContextHolder.setDefaultLocale(Locale.GERMAN);
		assertEquals(Locale.GERMAN, LocaleContextHolder.getLocale());
		LocaleContextHolder.setDefaultLocale(null);
		assertEquals(Locale.getDefault(), LocaleContextHolder.getLocale());
	}

	@Test
	public void testSetTimeZone() {
		LocaleContextHolder.setTimeZone(TimeZone.getTimeZone("GMT+1"));
		assertEquals(Locale.getDefault(), LocaleContextHolder.getLocale());
		assertEquals(TimeZone.getTimeZone("GMT+1"), LocaleContextHolder.getTimeZone());
		assertTrue(LocaleContextHolder.getLocaleContext() instanceof TimeZoneAwareLocaleContext);
		assertNull(LocaleContextHolder.getLocaleContext().getLocale());
		assertEquals(TimeZone.getTimeZone("GMT+1"), ((TimeZoneAwareLocaleContext) LocaleContextHolder.getLocaleContext()).getTimeZone());

		LocaleContextHolder.setTimeZone(TimeZone.getTimeZone("GMT+2"));
		assertEquals(Locale.getDefault(), LocaleContextHolder.getLocale());
		assertEquals(TimeZone.getTimeZone("GMT+2"), LocaleContextHolder.getTimeZone());
		assertTrue(LocaleContextHolder.getLocaleContext() instanceof TimeZoneAwareLocaleContext);
		assertNull(LocaleContextHolder.getLocaleContext().getLocale());
		assertEquals(TimeZone.getTimeZone("GMT+2"), ((TimeZoneAwareLocaleContext) LocaleContextHolder.getLocaleContext()).getTimeZone());

		LocaleContextHolder.setTimeZone(null);
		assertNull(LocaleContextHolder.getLocaleContext());
		assertEquals(Locale.getDefault(), LocaleContextHolder.getLocale());
		assertEquals(TimeZone.getDefault(), LocaleContextHolder.getTimeZone());

		LocaleContextHolder.setDefaultTimeZone(TimeZone.getTimeZone("GMT+1"));
		assertEquals(TimeZone.getTimeZone("GMT+1"), LocaleContextHolder.getTimeZone());
		LocaleContextHolder.setDefaultTimeZone(null);
		assertEquals(TimeZone.getDefault(), LocaleContextHolder.getTimeZone());
	}

	@Test
	public void testSetLocaleAndSetTimeZoneMixed() {
		LocaleContextHolder.setLocale(Locale.GERMANY);
		assertEquals(Locale.GERMANY, LocaleContextHolder.getLocale());
		assertEquals(TimeZone.getDefault(), LocaleContextHolder.getTimeZone());
		assertFalse(LocaleContextHolder.getLocaleContext() instanceof TimeZoneAwareLocaleContext);
		assertEquals(Locale.GERMANY, LocaleContextHolder.getLocaleContext().getLocale());

		LocaleContextHolder.setTimeZone(TimeZone.getTimeZone("GMT+1"));
		assertEquals(Locale.GERMANY, LocaleContextHolder.getLocale());
		assertEquals(TimeZone.getTimeZone("GMT+1"), LocaleContextHolder.getTimeZone());
		assertTrue(LocaleContextHolder.getLocaleContext() instanceof TimeZoneAwareLocaleContext);
		assertEquals(Locale.GERMANY, LocaleContextHolder.getLocaleContext().getLocale());
		assertEquals(TimeZone.getTimeZone("GMT+1"), ((TimeZoneAwareLocaleContext) LocaleContextHolder.getLocaleContext()).getTimeZone());

		LocaleContextHolder.setLocale(Locale.GERMAN);
		assertEquals(Locale.GERMAN, LocaleContextHolder.getLocale());
		assertEquals(TimeZone.getTimeZone("GMT+1"), LocaleContextHolder.getTimeZone());
		assertTrue(LocaleContextHolder.getLocaleContext() instanceof TimeZoneAwareLocaleContext);
		assertEquals(Locale.GERMAN, LocaleContextHolder.getLocaleContext().getLocale());
		assertEquals(TimeZone.getTimeZone("GMT+1"), ((TimeZoneAwareLocaleContext) LocaleContextHolder.getLocaleContext()).getTimeZone());

		LocaleContextHolder.setTimeZone(null);
		assertEquals(Locale.GERMAN, LocaleContextHolder.getLocale());
		assertEquals(TimeZone.getDefault(), LocaleContextHolder.getTimeZone());
		assertFalse(LocaleContextHolder.getLocaleContext() instanceof TimeZoneAwareLocaleContext);
		assertEquals(Locale.GERMAN, LocaleContextHolder.getLocaleContext().getLocale());

		LocaleContextHolder.setTimeZone(TimeZone.getTimeZone("GMT+2"));
		assertEquals(Locale.GERMAN, LocaleContextHolder.getLocale());
		assertEquals(TimeZone.getTimeZone("GMT+2"), LocaleContextHolder.getTimeZone());
		assertTrue(LocaleContextHolder.getLocaleContext() instanceof TimeZoneAwareLocaleContext);
		assertEquals(Locale.GERMAN, LocaleContextHolder.getLocaleContext().getLocale());
		assertEquals(TimeZone.getTimeZone("GMT+2"), ((TimeZoneAwareLocaleContext) LocaleContextHolder.getLocaleContext()).getTimeZone());

		LocaleContextHolder.setLocale(null);
		assertEquals(Locale.getDefault(), LocaleContextHolder.getLocale());
		assertEquals(TimeZone.getTimeZone("GMT+2"), LocaleContextHolder.getTimeZone());
		assertTrue(LocaleContextHolder.getLocaleContext() instanceof TimeZoneAwareLocaleContext);
		assertNull(LocaleContextHolder.getLocaleContext().getLocale());
		assertEquals(TimeZone.getTimeZone("GMT+2"), ((TimeZoneAwareLocaleContext) LocaleContextHolder.getLocaleContext()).getTimeZone());

		LocaleContextHolder.setTimeZone(null);
		assertEquals(Locale.getDefault(), LocaleContextHolder.getLocale());
		assertEquals(TimeZone.getDefault(), LocaleContextHolder.getTimeZone());
		assertNull(LocaleContextHolder.getLocaleContext());
	}

}
