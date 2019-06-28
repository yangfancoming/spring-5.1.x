

package org.springframework.format.datetime.standard;

import java.text.ParseException;
import java.time.Duration;
import java.util.Locale;

import org.springframework.format.Formatter;

/**
 * {@link Formatter} implementation for a JSR-310 {@link Duration},
 * following JSR-310's parsing rules for a Duration.
 *
 * @author Juergen Hoeller
 * @since 4.2.4
 * @see Duration#parse
 */
class DurationFormatter implements Formatter<Duration> {

	@Override
	public Duration parse(String text, Locale locale) throws ParseException {
		return Duration.parse(text);
	}

	@Override
	public String print(Duration object, Locale locale) {
		return object.toString();
	}

}
