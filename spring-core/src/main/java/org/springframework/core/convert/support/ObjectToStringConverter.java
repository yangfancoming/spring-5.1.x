

package org.springframework.core.convert.support;

import org.springframework.core.convert.converter.Converter;

/**
 * Simply calls {@link Object#toString()} to convert a source Object to a String.
 *
 * @author Keith Donald
 * @since 3.0
 */
final class ObjectToStringConverter implements Converter<Object, String> {

	@Override
	public String convert(Object source) {
		return source.toString();
	}

}
