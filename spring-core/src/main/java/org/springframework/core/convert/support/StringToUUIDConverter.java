

package org.springframework.core.convert.support;

import java.util.UUID;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/**
 * Converts from a String to a {@link java.util.UUID}.
 *
 * @author Phillip Webb
 * @since 3.2
 * @see UUID#fromString
 */
final class StringToUUIDConverter implements Converter<String, UUID> {

	@Override
	public UUID convert(String source) {
		return (StringUtils.hasLength(source) ? UUID.fromString(source.trim()) : null);
	}

}
