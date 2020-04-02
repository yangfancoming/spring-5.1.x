

package org.springframework.core.convert.support;

import java.util.Locale;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * Converts from a String to a {@link java.util.Locale}.
 *
 * Accepts the classic {@link Locale} String format ({@link Locale#toString()})
 * as well as BCP 47 language tags ({@link Locale#forLanguageTag} on Java 7+).
 *
 * @author Keith Donald

 * @since 3.0
 * @see StringUtils#parseLocale
 */
final class StringToLocaleConverter implements Converter<String, Locale> {

	@Override
	@Nullable
	public Locale convert(String source) {
		return StringUtils.parseLocale(source);
	}

}
