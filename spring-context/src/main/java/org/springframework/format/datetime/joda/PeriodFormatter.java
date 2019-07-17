

package org.springframework.format.datetime.joda;

import java.text.ParseException;
import java.util.Locale;

import org.joda.time.Period;

import org.springframework.format.Formatter;

/**
 * {@link Formatter} implementation for a Joda-Time {@link Period},
 * following Joda-Time's parsing rules for a Period.
 *

 * @since 4.2.4
 * @see Period#parse
 */
class PeriodFormatter implements Formatter<Period> {

	@Override
	public Period parse(String text, Locale locale) throws ParseException {
		return Period.parse(text);
	}

	@Override
	public String print(Period object, Locale locale) {
		return object.toString();
	}

}
