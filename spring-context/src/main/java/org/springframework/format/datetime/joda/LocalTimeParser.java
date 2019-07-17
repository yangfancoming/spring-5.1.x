

package org.springframework.format.datetime.joda;

import java.text.ParseException;
import java.util.Locale;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;

import org.springframework.format.Parser;

/**
 * Parses Joda {@link org.joda.time.LocalTime} instances using a
 * {@link org.joda.time.format.DateTimeFormatter}.
 *

 * @since 4.0
 */
public final class LocalTimeParser implements Parser<LocalTime> {

	private final DateTimeFormatter formatter;


	/**
	 * Create a new DateTimeParser.
	 * @param formatter the Joda DateTimeFormatter instance
	 */
	public LocalTimeParser(DateTimeFormatter formatter) {
		this.formatter = formatter;
	}


	@Override
	public LocalTime parse(String text, Locale locale) throws ParseException {
		return JodaTimeContextHolder.getFormatter(this.formatter, locale).parseLocalTime(text);
	}

}
