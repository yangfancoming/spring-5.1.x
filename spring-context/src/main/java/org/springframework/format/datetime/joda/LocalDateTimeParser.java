

package org.springframework.format.datetime.joda;

import java.text.ParseException;
import java.util.Locale;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;

import org.springframework.format.Parser;

/**
 * Parses Joda {@link org.joda.time.LocalDateTime} instances using a
 * {@link org.joda.time.format.DateTimeFormatter}.
 *
 * @author Juergen Hoeller
 * @since 4.0
 */
public final class LocalDateTimeParser implements Parser<LocalDateTime> {

	private final DateTimeFormatter formatter;


	/**
	 * Create a new DateTimeParser.
	 * @param formatter the Joda DateTimeFormatter instance
	 */
	public LocalDateTimeParser(DateTimeFormatter formatter) {
		this.formatter = formatter;
	}


	@Override
	public LocalDateTime parse(String text, Locale locale) throws ParseException {
		return JodaTimeContextHolder.getFormatter(this.formatter, locale).parseLocalDateTime(text);
	}

}
