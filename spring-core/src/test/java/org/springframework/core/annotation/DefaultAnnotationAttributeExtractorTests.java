

package org.springframework.core.annotation;

import java.lang.annotation.Annotation;

/**
 * Unit tests for {@link DefaultAnnotationAttributeExtractor}.
 *
 * @author Sam Brannen
 * @since 4.2.1
 */
public class DefaultAnnotationAttributeExtractorTests extends AbstractAliasAwareAnnotationAttributeExtractorTestCase {

	@Override
	protected AnnotationAttributeExtractor<?> createExtractorFor(Class<?> clazz, String expected, Class<? extends Annotation> annotationType) {
		return new DefaultAnnotationAttributeExtractor(clazz.getAnnotation(annotationType), clazz);
	}

}
