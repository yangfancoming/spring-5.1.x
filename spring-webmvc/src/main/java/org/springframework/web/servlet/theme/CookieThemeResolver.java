

package org.springframework.web.servlet.theme;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.util.CookieGenerator;
import org.springframework.web.util.WebUtils;

/**
 * {@link ThemeResolver} implementation that uses a cookie sent back to the user
 * in case of a custom setting, with a fallback to the default theme.
 * This is particularly useful for stateless applications without user sessions.
 *
 * Custom controllers can thus override the user's theme by calling
 * {@code setThemeName}, e.g. responding to a certain theme change request.
 * @since 17.06.2003
 * @see #setThemeName
 */
public class CookieThemeResolver extends CookieGenerator implements ThemeResolver {

	/**
	 * The default theme name used if no alternative is provided.
	 */
	public static final String ORIGINAL_DEFAULT_THEME_NAME = "theme";

	/**
	 * Name of the request attribute that holds the theme name. Only used
	 * for overriding a cookie value if the theme has been changed in the
	 * course of the current request! Use RequestContext.getTheme() to
	 * retrieve the current theme in controllers or views.
	 * @see org.springframework.web.servlet.support.RequestContext#getTheme
	 */
	public static final String THEME_REQUEST_ATTRIBUTE_NAME = CookieThemeResolver.class.getName() + ".THEME";

	/**
	 * The default name of the cookie that holds the theme name.
	 */
	public static final String DEFAULT_COOKIE_NAME = CookieThemeResolver.class.getName() + ".THEME";


	private String defaultThemeName = ORIGINAL_DEFAULT_THEME_NAME;


	public CookieThemeResolver() {
		setCookieName(DEFAULT_COOKIE_NAME);
	}


	/**
	 * Set the name of the default theme.
	 */
	public void setDefaultThemeName(String defaultThemeName) {
		this.defaultThemeName = defaultThemeName;
	}

	/**
	 * Return the name of the default theme.
	 */
	public String getDefaultThemeName() {
		return this.defaultThemeName;
	}


	@Override
	public String resolveThemeName(HttpServletRequest request) {
		// Check request for preparsed or preset theme.
		String themeName = (String) request.getAttribute(THEME_REQUEST_ATTRIBUTE_NAME);
		if (themeName != null) {
			return themeName;
		}
		// Retrieve cookie value from request.
		String cookieName = getCookieName();
		if (cookieName != null) {
			Cookie cookie = WebUtils.getCookie(request, cookieName);
			if (cookie != null) {
				String value = cookie.getValue();
				if (StringUtils.hasText(value)) {
					themeName = value;
				}
			}
		}

		// Fall back to default theme.
		if (themeName == null) {
			themeName = getDefaultThemeName();
		}
		request.setAttribute(THEME_REQUEST_ATTRIBUTE_NAME, themeName);
		return themeName;
	}

	@Override
	public void setThemeName(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable String themeName) {
		Assert.notNull(response, "HttpServletResponse is required for CookieThemeResolver");
		if (StringUtils.hasText(themeName)) {
			// Set request attribute and add cookie.
			request.setAttribute(THEME_REQUEST_ATTRIBUTE_NAME, themeName);
			addCookie(response, themeName);
		}
		else {
			// Set request attribute to fallback theme and remove cookie.
			request.setAttribute(THEME_REQUEST_ATTRIBUTE_NAME, getDefaultThemeName());
			removeCookie(response);
		}
	}

}
