

package org.springframework.format.datetime.joda;

import java.text.ParseException;
import java.util.Locale;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

import org.springframework.format.Parser;

/**
 * Parses Joda {@link org.joda.time.LocalDate} instances using a
 * {@link org.joda.time.format.DateTimeFormatter}.
 *
 * @author Juergen Hoeller
 * @since 4.0
 */
public final class LocalDateParser implements Parser<LocalDate> {

	private final DateTimeFormatter formatter;


	/**
	 * Create a new DateTimeParser.
	 * @param formatter the Joda DateTimeFormatter instance
	 */
	public LocalDateParser(DateTimeFormatter formatter) {
		this.formatter = formatter;
	}


	@Override
	public LocalDate parse(String text, Locale locale) throws ParseException {
		return JodaTimeContextHolder.getFormatter(this.formatter, locale).parseLocalDate(text);
	}

}
