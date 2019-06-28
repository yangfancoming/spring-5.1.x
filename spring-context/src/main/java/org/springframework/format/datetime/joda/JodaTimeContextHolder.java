

package org.springframework.format.datetime.joda;

import java.util.Locale;

import org.joda.time.format.DateTimeFormatter;

import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;

/**
 * A holder for a thread-local {@link JodaTimeContext}
 * with user-specific Joda-Time settings.
 *
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 3.0
 * @see org.springframework.context.i18n.LocaleContextHolder
 */
public final class JodaTimeContextHolder {

	private static final ThreadLocal<JodaTimeContext> jodaTimeContextHolder =
			new NamedThreadLocal<>("JodaTimeContext");


	private JodaTimeContextHolder() {
	}


	/**
	 * Reset the JodaTimeContext for the current thread.
	 */
	public static void resetJodaTimeContext() {
		jodaTimeContextHolder.remove();
	}

	/**
	 * Associate the given JodaTimeContext with the current thread.
	 * @param jodaTimeContext the current JodaTimeContext,
	 * or {@code null} to reset the thread-bound context
	 */
	public static void setJodaTimeContext(@Nullable JodaTimeContext jodaTimeContext) {
		if (jodaTimeContext == null) {
			resetJodaTimeContext();
		}
		else {
			jodaTimeContextHolder.set(jodaTimeContext);
		}
	}

	/**
	 * Return the JodaTimeContext associated with the current thread, if any.
	 * @return the current JodaTimeContext, or {@code null} if none
	 */
	@Nullable
	public static JodaTimeContext getJodaTimeContext() {
		return jodaTimeContextHolder.get();
	}


	/**
	 * Obtain a DateTimeFormatter with user-specific settings applied to the given base Formatter.
	 * @param formatter the base formatter that establishes default formatting rules
	 * (generally user independent)
	 * @param locale the current user locale (may be {@code null} if not known)
	 * @return the user-specific DateTimeFormatter
	 */
	public static DateTimeFormatter getFormatter(DateTimeFormatter formatter, @Nullable Locale locale) {
		DateTimeFormatter formatterToUse = (locale != null ? formatter.withLocale(locale) : formatter);
		JodaTimeContext context = getJodaTimeContext();
		return (context != null ? context.getFormatter(formatterToUse) : formatterToUse);
	}

}
