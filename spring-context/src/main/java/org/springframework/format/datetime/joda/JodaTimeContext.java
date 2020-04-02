

package org.springframework.format.datetime.joda;

import java.util.TimeZone;

import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.lang.Nullable;

/**
 * A context that holds user-specific Joda-Time settings such as the user's
 * Chronology (calendar system) and time zone.
 *
 * A {@code null} property value indicate the user has not specified a setting.
 *
 * @author Keith Donald
 * @since 3.0
 * @see JodaTimeContextHolder
 */
public class JodaTimeContext {

	@Nullable
	private Chronology chronology;

	@Nullable
	private DateTimeZone timeZone;


	/**
	 * Set the user's chronology (calendar system).
	 */
	public void setChronology(@Nullable Chronology chronology) {
		this.chronology = chronology;
	}

	/**
	 * Return the user's chronology (calendar system), if any.
	 */
	@Nullable
	public Chronology getChronology() {
		return this.chronology;
	}

	/**
	 * Set the user's time zone.
	 * Alternatively, set a {@link TimeZoneAwareLocaleContext} on
	 * {@link LocaleContextHolder}. This context class will fall back to
	 * checking the locale context if no setting has been provided here.
	 * @see org.springframework.context.i18n.LocaleContextHolder#getTimeZone()
	 * @see org.springframework.context.i18n.LocaleContextHolder#setLocaleContext
	 */
	public void setTimeZone(@Nullable DateTimeZone timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * Return the user's time zone, if any.
	 */
	@Nullable
	public DateTimeZone getTimeZone() {
		return this.timeZone;
	}


	/**
	 * Get the DateTimeFormatter with the this context's settings
	 * applied to the base {@code formatter}.
	 * @param formatter the base formatter that establishes default
	 * formatting rules, generally context-independent
	 * @return the contextual DateTimeFormatter
	 */
	public DateTimeFormatter getFormatter(DateTimeFormatter formatter) {
		if (this.chronology != null) {
			formatter = formatter.withChronology(this.chronology);
		}
		if (this.timeZone != null) {
			formatter = formatter.withZone(this.timeZone);
		}
		else {
			LocaleContext localeContext = LocaleContextHolder.getLocaleContext();
			if (localeContext instanceof TimeZoneAwareLocaleContext) {
				TimeZone timeZone = ((TimeZoneAwareLocaleContext) localeContext).getTimeZone();
				if (timeZone != null) {
					formatter = formatter.withZone(DateTimeZone.forTimeZone(timeZone));
				}
			}
		}
		return formatter;
	}

}
