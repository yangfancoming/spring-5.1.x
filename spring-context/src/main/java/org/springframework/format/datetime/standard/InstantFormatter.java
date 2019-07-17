

package org.springframework.format.datetime.standard;

import java.text.ParseException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.format.Formatter;

/**
 * {@link Formatter} implementation for a JSR-310 {@link java.time.Instant},
 * following JSR-310's parsing rules for an Instant (that is, not using a
 * configurable {@link java.time.format.DateTimeFormatter}): accepting the
 * default {@code ISO_INSTANT} format as well as {@code RFC_1123_DATE_TIME}
 * (which is commonly used for HTTP date header values), as of Spring 4.3.
 *

 * @since 4.0
 * @see java.time.Instant#parse
 * @see java.time.format.DateTimeFormatter#ISO_INSTANT
 * @see java.time.format.DateTimeFormatter#RFC_1123_DATE_TIME
 */
public class InstantFormatter implements Formatter<Instant> {

	@Override
	public Instant parse(String text, Locale locale) throws ParseException {
		if (text.length() > 0 && Character.isDigit(text.charAt(0))) {
			// assuming UTC instant a la "2007-12-03T10:15:30.00Z"
			return Instant.parse(text);
		}
		else {
			// assuming RFC-1123 value a la "Tue, 3 Jun 2008 11:05:30 GMT"
			return Instant.from(DateTimeFormatter.RFC_1123_DATE_TIME.parse(text));
		}
	}

	@Override
	public String print(Instant object, Locale locale) {
		return object.toString();
	}

}
