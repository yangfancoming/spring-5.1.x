

package org.springframework.format.datetime.joda;

import java.text.ParseException;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import org.springframework.format.Parser;

/**
 * Parses Joda {@link DateTime} instances using a {@link DateTimeFormatter}.
 *
 * @author Keith Donald
 * @since 3.0
 */
public final class DateTimeParser implements Parser<DateTime> {

	private final DateTimeFormatter formatter;


	/**
	 * Create a new DateTimeParser.
	 * @param formatter the Joda DateTimeFormatter instance
	 */
	public DateTimeParser(DateTimeFormatter formatter) {
		this.formatter = formatter;
	}


	@Override
	public DateTime parse(String text, Locale locale) throws ParseException {
		return JodaTimeContextHolder.getFormatter(this.formatter, locale).parseDateTime(text);
	}

}
