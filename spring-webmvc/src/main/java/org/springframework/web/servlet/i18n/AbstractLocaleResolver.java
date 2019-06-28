

package org.springframework.web.servlet.i18n;

import java.util.Locale;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.LocaleResolver;

/**
 * Abstract base class for {@link LocaleResolver} implementations.
 * Provides support for a default locale.
 *
 * @author Juergen Hoeller
 * @since 1.2.9
 * @see #setDefaultLocale
 */
public abstract class AbstractLocaleResolver implements LocaleResolver {

	@Nullable
	private Locale defaultLocale;


	/**
	 * Set a default Locale that this resolver will return if no other locale found.
	 */
	public void setDefaultLocale(@Nullable Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	/**
	 * Return the default Locale that this resolver is supposed to fall back to, if any.
	 */
	@Nullable
	protected Locale getDefaultLocale() {
		return this.defaultLocale;
	}

}
