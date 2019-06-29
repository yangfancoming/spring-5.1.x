

package org.springframework.core.convert.support;

import java.util.Currency;

import org.springframework.core.convert.converter.Converter;

/**
 * Convert a String to a {@link Currency}.
 *
 * @author Stephane Nicoll
 * @since 4.2
 */
class StringToCurrencyConverter implements Converter<String, Currency> {

	@Override
	public Currency convert(String source) {
		return Currency.getInstance(source);
	}

}
