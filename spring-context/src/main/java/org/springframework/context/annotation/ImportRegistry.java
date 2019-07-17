

package org.springframework.context.annotation;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;

/**
 * Registry of imported class {@link AnnotationMetadata}.
 *

 * @author Phillip Webb
 */
interface ImportRegistry {

	@Nullable
	AnnotationMetadata getImportingClassFor(String importedClass);

	void removeImportingClass(String importingClass);

}
