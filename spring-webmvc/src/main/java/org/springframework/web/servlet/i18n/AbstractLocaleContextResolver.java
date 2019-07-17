

package org.springframework.web.servlet.i18n;

import java.util.Locale;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.LocaleContextResolver;

/**
 * Abstract base class for {@link LocaleContextResolver} implementations.
 * Provides support for a default locale and a default time zone.
 *
 * <p>Also provides pre-implemented versions of {@link #resolveLocale} and {@link #setLocale},
 * delegating to {@link #resolveLocaleContext} and {@link #setLocaleContext}.
 *

 * @since 4.0
 * @see #setDefaultLocale
 * @see #setDefaultTimeZone
 */
public abstract class AbstractLocaleContextResolver extends AbstractLocaleResolver implements LocaleContextResolver {

	@Nullable
	private TimeZone defaultTimeZone;


	/**
	 * Set a default TimeZone that this resolver will return if no other time zone found.
	 */
	public void setDefaultTimeZone(@Nullable TimeZone defaultTimeZone) {
		this.defaultTimeZone = defaultTimeZone;
	}

	/**
	 * Return the default TimeZone that this resolver is supposed to fall back to, if any.
	 */
	@Nullable
	public TimeZone getDefaultTimeZone() {
		return this.defaultTimeZone;
	}


	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		Locale locale = resolveLocaleContext(request).getLocale();
		return (locale != null ? locale : request.getLocale());
	}

	@Override
	public void setLocale(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Locale locale) {
		setLocaleContext(request, response, (locale != null ? new SimpleLocaleContext(locale) : null));
	}

}
