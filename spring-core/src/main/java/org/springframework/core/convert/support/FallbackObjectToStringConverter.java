

package org.springframework.core.convert.support;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Set;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.lang.Nullable;

/**
 * Simply calls {@link Object#toString()} to convert any supported object
 * to a {@link String}.
 *
 * Supports {@link CharSequence}, {@link StringWriter}, and any class
 * with a String constructor or one of the following static factory methods:
 * {@code valueOf(String)}, {@code of(String)}, {@code from(String)}.
 *
 * Used by the {@link DefaultConversionService} as a fallback if there
 * are no other explicit to-String converters registered.
 *
 * @author Keith Donald

 * @author Sam Brannen
 * @since 3.0
 * @see ObjectToObjectConverter
 */
final class FallbackObjectToStringConverter implements ConditionalGenericConverter {

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object.class, String.class));
	}

	@Override
	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		Class<?> sourceClass = sourceType.getObjectType();
		if (String.class == sourceClass) {
			// no conversion required
			return false;
		}
		return (CharSequence.class.isAssignableFrom(sourceClass) ||
				StringWriter.class.isAssignableFrom(sourceClass) ||
				ObjectToObjectConverter.hasConversionMethodOrConstructor(sourceClass, String.class));
	}

	@Override
	@Nullable
	public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		return (source != null ? source.toString() : null);
	}

}
