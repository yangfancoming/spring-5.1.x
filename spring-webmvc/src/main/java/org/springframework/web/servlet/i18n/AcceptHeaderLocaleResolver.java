

package org.springframework.web.servlet.i18n;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;

/**
 * {@link LocaleResolver} implementation that simply uses the primary locale
 * specified in the "accept-language" header of the HTTP request (that is,
 * the locale sent by the client browser, normally that of the client's OS).
 *
 * Note: Does not support {@code setLocale}, since the accept header
 * can only be changed through changing the client's locale settings.

 * @since 27.02.2003
 * @see javax.servlet.http.HttpServletRequest#getLocale()
 */
public class AcceptHeaderLocaleResolver implements LocaleResolver {

	private final List<Locale> supportedLocales = new ArrayList<>(4);

	@Nullable
	private Locale defaultLocale;

	/**
	 * Configure supported locales to check against the requested locales
	 * determined via {@link HttpServletRequest#getLocales()}. If this is not
	 * configured then {@link HttpServletRequest#getLocale()} is used instead.
	 * @param locales the supported locales
	 * @since 4.3
	 */
	public void setSupportedLocales(List<Locale> locales) {
		this.supportedLocales.clear();
		this.supportedLocales.addAll(locales);
	}

	/**
	 * Return the configured list of supported locales.
	 * @since 4.3
	 */
	public List<Locale> getSupportedLocales() {
		return this.supportedLocales;
	}

	/**
	 * Configure a fixed default locale to fall back on if the request does not
	 * have an "Accept-Language" header.
	 * By default this is not set in which case when there is "Accept-Language"
	 * header, the default locale for the server is used as defined in
	 * {@link HttpServletRequest#getLocale()}.
	 * @param defaultLocale the default locale to use
	 * @since 4.3
	 */
	public void setDefaultLocale(@Nullable Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	/**
	 * The configured default locale, if any.
	 * @since 4.3
	 */
	@Nullable
	public Locale getDefaultLocale() {
		return this.defaultLocale;
	}


	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		// 获取默认设置，可在配置AcceptHeaderLocaleResolver Bean中设置defaultLocale属性
		Locale defaultLocale = getDefaultLocale();
		// 设置了默认值并且请求中没有Accept-Language头信息时，使用默认设置
		if (defaultLocale != null && request.getHeader("Accept-Language") == null) {
			return defaultLocale;
		}
		// 从当前请求中获取Locale
		Locale requestLocale = request.getLocale();
		// 从配置中获取支持的Locale集合，可在AcceptHeaderLocaleResolver Bean中设置supportedLocales属性
		List<Locale> supportedLocales = getSupportedLocales();
		// 未设置supportedLocales或者supportedLocales中包括请求Locale，则使用请求Locale
		if (supportedLocales.isEmpty() || supportedLocales.contains(requestLocale)) {
			return requestLocale;
		}
		// 找到设置的Locale集合中是否有请求的Locale
		Locale supportedLocale = findSupportedLocale(request, supportedLocales);
		if (supportedLocale != null) {
			return supportedLocale;
		}
		return (defaultLocale != null ? defaultLocale : requestLocale);
	}

	@Nullable
	private Locale findSupportedLocale(HttpServletRequest request, List<Locale> supportedLocales) {
		Enumeration<Locale> requestLocales = request.getLocales();
		Locale languageMatch = null;
		while (requestLocales.hasMoreElements()) {
			Locale locale = requestLocales.nextElement();
			if (supportedLocales.contains(locale)) {
				if (languageMatch == null || languageMatch.getLanguage().equals(locale.getLanguage())) {
					// Full match: language + country, possibly narrowed from earlier language-only match
					return locale;
				}
			}
			else if (languageMatch == null) {
				// Let's try to find a language-only match as a fallback
				for (Locale candidate : supportedLocales) {
					if (!StringUtils.hasLength(candidate.getCountry()) && candidate.getLanguage().equals(locale.getLanguage())) {
						languageMatch = candidate;
						break;
					}
				}
			}
		}
		return languageMatch;
	}

	/**
	 * 不支持程序设置Locale
	 */
	@Override
	public void setLocale(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Locale locale) {
		throw new UnsupportedOperationException("Cannot change HTTP accept header - use a different locale resolution strategy");
	}

}
