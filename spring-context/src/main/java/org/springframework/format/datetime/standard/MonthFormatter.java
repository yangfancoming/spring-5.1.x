

package org.springframework.format.datetime.standard;

import java.text.ParseException;
import java.time.Month;
import java.util.Locale;

import org.springframework.format.Formatter;

/**
 * {@link Formatter} implementation for a JSR-310 {@link Month},
 * resolving a given String against the Month enum values (ignoring case).
 *

 * @since 5.0.4
 * @see Month#valueOf
 */
class MonthFormatter implements Formatter<Month> {

	@Override
	public Month parse(String text, Locale locale) throws ParseException {
		return Month.valueOf(text.toUpperCase());
	}

	@Override
	public String print(Month object, Locale locale) {
		return object.toString();
	}

}
