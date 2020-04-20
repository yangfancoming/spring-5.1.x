

package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

/**
 * Default implementation of the {@link AnnotationAttributeExtractor} strategy that is backed by an {@link Annotation}.
 * @since 4.2
 * @see Annotation
 * @see AliasFor
 * @see AbstractAliasAwareAnnotationAttributeExtractor
 * @see MapAnnotationAttributeExtractor
 * @see AnnotationUtils#synthesizeAnnotation
 */
class DefaultAnnotationAttributeExtractor extends AbstractAliasAwareAnnotationAttributeExtractor<Annotation> {

	/**
	 * Construct a new {@code DefaultAnnotationAttributeExtractor}.
	 * @param annotation the annotation to synthesize; never {@code null}
	 * @param annotatedElement the element that is annotated with the supplied
	 * annotation; may be {@code null} if unknown
	 */
	DefaultAnnotationAttributeExtractor(Annotation annotation, @Nullable Object annotatedElement) {
		super(annotation.annotationType(), annotatedElement, annotation);
	}


	@Override
	@Nullable
	protected Object getRawAttributeValue(Method attributeMethod) {
		ReflectionUtils.makeAccessible(attributeMethod);
		return ReflectionUtils.invokeMethod(attributeMethod, getSource());
	}

	@Override
	@Nullable
	protected Object getRawAttributeValue(String attributeName) {
		Method attributeMethod = ReflectionUtils.findMethod(getAnnotationType(), attributeName);
		return (attributeMethod != null ? getRawAttributeValue(attributeMethod) : null);
	}

}
