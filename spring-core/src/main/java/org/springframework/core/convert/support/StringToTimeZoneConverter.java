

package org.springframework.core.convert.support;

import java.util.TimeZone;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/**
 * Convert a String to a {@link TimeZone}.
 *
 * @author Stephane Nicoll
 * @since 4.2
 */
class StringToTimeZoneConverter implements Converter<String, TimeZone> {

	@Override
	public TimeZone convert(String source) {
		return StringUtils.parseTimeZoneString(source);
	}

}
