

package org.springframework.core.convert.converter;

import org.springframework.core.convert.TypeDescriptor;

/**
 * A {@link GenericConverter} that may conditionally execute based on attributes
 * of the {@code source} and {@code target} {@link TypeDescriptor}.
 *
 * See {@link ConditionalConverter} for details.
 *
 * @author Keith Donald
 * @author Phillip Webb
 * @since 3.0
 * @see GenericConverter
 * @see ConditionalConverter
 */
public interface ConditionalGenericConverter extends GenericConverter, ConditionalConverter {

}
