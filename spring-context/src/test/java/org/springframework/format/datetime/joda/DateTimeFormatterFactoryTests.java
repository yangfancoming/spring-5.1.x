

package org.springframework.format.datetime.joda;

import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import org.springframework.format.annotation.DateTimeFormat.ISO;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * @author Phillip Webb
 * @author Sam Brannen
 */
public class DateTimeFormatterFactoryTests {

	// Potential test timezone, both have daylight savings on October 21st
	private static final TimeZone ZURICH = TimeZone.getTimeZone("Europe/Zurich");
	private static final TimeZone NEW_YORK = TimeZone.getTimeZone("America/New_York");

	// Ensure that we are testing against a timezone other than the default.
	private static final TimeZone TEST_TIMEZONE = ZURICH.equals(TimeZone.getDefault()) ? NEW_YORK : ZURICH;


	private DateTimeFormatterFactory factory = new DateTimeFormatterFactory();

	private DateTime dateTime = new DateTime(2009, 10, 21, 12, 10, 00, 00);


	@Test
	public void createDateTimeFormatter() {
		assertThat(factory.createDateTimeFormatter(), is(equalTo(DateTimeFormat.mediumDateTime())));
	}

	@Test
	public void createDateTimeFormatterWithPattern() {
		factory = new DateTimeFormatterFactory("yyyyMMddHHmmss");
		DateTimeFormatter formatter = factory.createDateTimeFormatter();
		assertThat(formatter.print(dateTime), is("20091021121000"));
	}

	@Test
	public void createDateTimeFormatterWithNullFallback() {
		DateTimeFormatter formatter = factory.createDateTimeFormatter(null);
		assertThat(formatter, is(nullValue()));
	}

	@Test
	public void createDateTimeFormatterWithFallback() {
		DateTimeFormatter fallback = DateTimeFormat.forStyle("LL");
		DateTimeFormatter formatter = factory.createDateTimeFormatter(fallback);
		assertThat(formatter, is(sameInstance(fallback)));
	}

	@Test
	public void createDateTimeFormatterInOrderOfPropertyPriority() {
		factory.setStyle("SS");
		String value = applyLocale(factory.createDateTimeFormatter()).print(dateTime);
		assertTrue(value.startsWith("10/21/09"));
		assertTrue(value.endsWith("12:10 PM"));

		factory.setIso(ISO.DATE);
		assertThat(applyLocale(factory.createDateTimeFormatter()).print(dateTime), is("2009-10-21"));

		factory.setPattern("yyyyMMddHHmmss");
		assertThat(factory.createDateTimeFormatter().print(dateTime), is("20091021121000"));
	}

	@Test
	public void createDateTimeFormatterWithTimeZone() {
		factory.setPattern("yyyyMMddHHmmss Z");
		factory.setTimeZone(TEST_TIMEZONE);
		DateTimeZone dateTimeZone = DateTimeZone.forTimeZone(TEST_TIMEZONE);
		DateTime dateTime = new DateTime(2009, 10, 21, 12, 10, 00, 00, dateTimeZone);
		String offset = (TEST_TIMEZONE.equals(NEW_YORK) ? "-0400" : "+0200");
		assertThat(factory.createDateTimeFormatter().print(dateTime), is("20091021121000 " + offset));
	}

	private DateTimeFormatter applyLocale(DateTimeFormatter dateTimeFormatter) {
		return dateTimeFormatter.withLocale(Locale.US);
	}

}
