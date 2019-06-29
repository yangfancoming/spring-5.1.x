

package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.springframework.lang.Nullable;

/**
 * An {@code AnnotationAttributeExtractor} is responsible for
 * {@linkplain #getAttributeValue extracting} annotation attribute values
 * from an underlying {@linkplain #getSource source} such as an
 * {@code Annotation} or a {@code Map}.
 *
 * @author Sam Brannen
 * @since 4.2
 * @param <S> the type of source supported by this extractor
 * @see SynthesizedAnnotationInvocationHandler
 */
interface AnnotationAttributeExtractor<S> {

	/**
	 * Get the type of annotation that this extractor extracts attribute
	 * values for.
	 */
	Class<? extends Annotation> getAnnotationType();

	/**
	 * Get the element that is annotated with an annotation of the annotation
	 * type supported by this extractor.
	 * @return the annotated element, or {@code null} if unknown
	 */
	@Nullable
	Object getAnnotatedElement();

	/**
	 * Get the underlying source of annotation attributes.
	 */
	S getSource();

	/**
	 * Get the attribute value from the underlying {@linkplain #getSource source}
	 * that corresponds to the supplied attribute method.
	 * @param attributeMethod an attribute method from the annotation type
	 * supported by this extractor
	 * @return the value of the annotation attribute
	 */
	@Nullable
	Object getAttributeValue(Method attributeMethod);

}
