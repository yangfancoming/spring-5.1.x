

package org.springframework.core.type.classreading;

import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;

/**
 * Simple facade for accessing class metadata,as read by an ASM {@link org.springframework.asm.ClassReader}.
 * @since 2.5
 */
public interface MetadataReader {

	/**
	 * Return the resource reference for the class file.
	 */
	Resource getResource();

	/**
	 * Read basic class metadata for the underlying class.
	 */
	ClassMetadata getClassMetadata();

	/**
	 * 读取基础类的完整注解元数据，包括注解方法的元数据。
	 * Read full annotation metadata for the underlying class,including metadata for annotated methods.
	 */
	AnnotationMetadata getAnnotationMetadata();
}
