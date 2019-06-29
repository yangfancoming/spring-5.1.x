

package org.springframework.core.convert.support;

import java.nio.charset.Charset;

import org.springframework.core.convert.converter.Converter;

/**
 * Convert a String to a {@link Charset}.
 *
 * @author Stephane Nicoll
 * @since 4.2
 */
class StringToCharsetConverter implements Converter<String, Charset> {

	@Override
	public Charset convert(String source) {
		return Charset.forName(source);
	}

}
