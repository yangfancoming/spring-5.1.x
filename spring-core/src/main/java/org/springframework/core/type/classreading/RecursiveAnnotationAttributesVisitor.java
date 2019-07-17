

package org.springframework.core.type.classreading;

import org.springframework.asm.AnnotationVisitor;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;

/**
 * {@link AnnotationVisitor} to recursively visit annotation attributes.


 * @since 3.1.1
 */
class RecursiveAnnotationAttributesVisitor extends AbstractRecursiveAnnotationVisitor {

	protected final String annotationType;


	public RecursiveAnnotationAttributesVisitor(
			String annotationType, AnnotationAttributes attributes, @Nullable ClassLoader classLoader) {

		super(classLoader, attributes);
		this.annotationType = annotationType;
	}


	@Override
	public void visitEnd() {
		AnnotationUtils.registerDefaultValues(this.attributes);
	}

}
