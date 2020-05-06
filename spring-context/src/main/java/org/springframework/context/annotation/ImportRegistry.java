

package org.springframework.context.annotation;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;

/**
 * Registry of imported class {@link AnnotationMetadata}.
 */
interface ImportRegistry {

	@Nullable
	AnnotationMetadata getImportingClassFor(String importedClass);

	void removeImportingClass(String importingClass);
}
