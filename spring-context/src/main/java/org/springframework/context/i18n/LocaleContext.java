

package org.springframework.context.i18n;

import java.util.Locale;

import org.springframework.lang.Nullable;

/**
 * Strategy interface for determining the current Locale.
 *
 * <p>A LocaleContext instance can be associated with a thread
 * via the LocaleContextHolder class.
 *

 * @since 1.2
 * @see LocaleContextHolder#getLocale()
 * @see TimeZoneAwareLocaleContext
 */
public interface LocaleContext {

	/**
	 * Return the current Locale, which can be fixed or determined dynamically,
	 * depending on the implementation strategy.
	 * @return the current Locale, or {@code null} if no specific Locale associated
	 */
	@Nullable
	Locale getLocale();

}
