

package org.springframework.format.datetime.joda;

import java.text.ParseException;
import java.util.Locale;

import org.joda.time.YearMonth;

import org.springframework.format.Formatter;

/**
 * {@link Formatter} implementation for a Joda-Time {@link YearMonth},
 * following Joda-Time's parsing rules for a YearMonth.
 *
 * @author Juergen Hoeller
 * @since 4.2.4
 * @see YearMonth#parse
 */
class YearMonthFormatter implements Formatter<YearMonth> {

	@Override
	public YearMonth parse(String text, Locale locale) throws ParseException {
		return YearMonth.parse(text);
	}

	@Override
	public String print(YearMonth object, Locale locale) {
		return object.toString();
	}

}
