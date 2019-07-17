

package org.springframework.format.datetime.joda;

import java.text.ParseException;
import java.util.Locale;

import org.joda.time.MonthDay;

import org.springframework.format.Formatter;

/**
 * {@link Formatter} implementation for a Joda-Time {@link MonthDay},
 * following Joda-Time's parsing rules for a MonthDay.
 *

 * @since 4.2.4
 * @see MonthDay#parse
 */
class MonthDayFormatter implements Formatter<MonthDay> {

	@Override
	public MonthDay parse(String text, Locale locale) throws ParseException {
		return MonthDay.parse(text);
	}

	@Override
	public String print(MonthDay object, Locale locale) {
		return object.toString();
	}

}
