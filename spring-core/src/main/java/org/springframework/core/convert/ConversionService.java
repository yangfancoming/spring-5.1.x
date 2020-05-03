

package org.springframework.core.convert;

import org.springframework.lang.Nullable;

/**
 * A service interface for type conversion. This is the entry point into the convert system.
 * Call {@link #convert(Object, Class)} to perform a thread-safe type conversion using this system.
 * 这种类型的bean最实用的场景就是用来将前端传过来的参数和后端的controller方法上的参数格式转换的时候使用
 * @since 3.0
 */
public interface ConversionService {

	/**
	 * Return {@code true} if objects of {@code sourceType} can be converted to the {@code targetType}.
	 * If this method returns {@code true}, it means {@link #convert(Object, Class)} is capable of converting an instance of {@code sourceType} to {@code targetType}.
	 * Special note on collections, arrays, and maps types:
	 * For conversion between collection, array, and map types, this method will return {@code true} even though a convert invocation may still generate a {@link ConversionException} if the underlying elements are not convertible.
	 * Callers are expected to handle this exceptional case when working with collections and maps.
	 * @param sourceType the source type to convert from (may be {@code null} if source is {@code null})
	 * @param targetType the target type to convert to (required)
	 * @return {@code true} if a conversion can be performed, {@code false} if not
	 * @throws IllegalArgumentException if {@code targetType} is {@code null}
	 */
	boolean canConvert(@Nullable Class<?> sourceType, Class<?> targetType);

	/**
	 * Return {@code true} if objects of {@code sourceType} can be converted to the {@code targetType}.
	 * The TypeDescriptors provide additional context about the source and target locations where conversion would occur, often object fields or property locations.
	 * If this method returns {@code true}, it means {@link #convert(Object, TypeDescriptor, TypeDescriptor)}
	 * is capable of converting an instance of {@code sourceType} to {@code targetType}.
	 * Special note on collections, arrays, and maps types:
	 * For conversion between collection, array, and map types, this method will return {@code true} even though a convert invocation may still generate a {@link ConversionException} if the  underlying elements are not convertible.
	 * Callers are expected to handle this exceptional case when working with collections and maps.
	 * @param sourceType context about the source type to convert from (may be {@code null} if source is {@code null})
	 * @param targetType context about the target type to convert to (required)
	 * @return {@code true} if a conversion can be performed between the source and target types,{@code false} if not
	 * @throws IllegalArgumentException if {@code targetType} is {@code null}
	 */
	boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType);

	/**
	 * Convert the given {@code source} to the specified {@code targetType}.
	 * @param source the source object to convert (may be {@code null})
	 * @param targetType the target type to convert to (required)
	 * @return the converted object, an instance of targetType
	 * @throws ConversionException if a conversion exception occurred
	 * @throws IllegalArgumentException if targetType is {@code null}
	 */
	@Nullable
	<T> T convert(@Nullable Object source, Class<T> targetType);

	/**
	 * Convert the given {@code source} to the specified {@code targetType}.
	 * The TypeDescriptors provide additional context about the source and target locations where conversion will occur, often object fields or property locations.
	 * @param source the source object to convert (may be {@code null})
	 * @param sourceType context about the source type to convert from (may be {@code null} if source is {@code null})
	 * @param targetType context about the target type to convert to (required)
	 * @return the converted object, an instance of {@link TypeDescriptor#getObjectType() targetType}
	 * @throws ConversionException if a conversion exception occurred
	 * @throws IllegalArgumentException if targetType is {@code null},or {@code sourceType} is {@code null} but source is not {@code null}
	 */
	@Nullable
	Object convert(@Nullable Object source, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType);

}
