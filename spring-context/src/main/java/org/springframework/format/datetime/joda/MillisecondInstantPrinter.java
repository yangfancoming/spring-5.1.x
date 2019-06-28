

package org.springframework.format.datetime.joda;

import java.util.Locale;

import org.joda.time.format.DateTimeFormatter;

import org.springframework.format.Printer;

/**
 * Prints Long instances using a Joda {@link DateTimeFormatter}.
 *
 * @author Keith Donald
 * @since 3.0
 */
public final class MillisecondInstantPrinter implements Printer<Long> {

	private final DateTimeFormatter formatter;


	/**
	 * Create a new ReadableInstantPrinter.
	 * @param formatter the Joda DateTimeFormatter instance
	 */
	public MillisecondInstantPrinter(DateTimeFormatter formatter) {
		this.formatter = formatter;
	}


	@Override
	public String print(Long instant, Locale locale) {
		return JodaTimeContextHolder.getFormatter(this.formatter, locale).print(instant);
	}

}
