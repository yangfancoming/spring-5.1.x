

package org.springframework.core.type.classreading;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.asm.ClassReader;
import org.springframework.core.NestedIOException;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.lang.Nullable;

/**
 * {@link MetadataReader} implementation based on an ASM {@link org.springframework.asm.ClassReader}.
 * Package-visible in order to allow for repackaging the ASM library without effect on users of the {@code core.type} package.
 * @since 2.5
 */
final class SimpleMetadataReader implements MetadataReader {

	private final Resource resource;

	private final ClassMetadata classMetadata;

	private final AnnotationMetadata annotationMetadata;

	SimpleMetadataReader(Resource resource, @Nullable ClassLoader classLoader) throws IOException {
		InputStream is = new BufferedInputStream(resource.getInputStream());
		ClassReader classReader;
		try {
			classReader = new ClassReader(is);
		}catch (IllegalArgumentException ex) {
			throw new NestedIOException("ASM ClassReader failed to parse class file - probably due to a new Java class file version that isn't supported yet: " + resource, ex);
		}
		finally {
			is.close();
		}
		AnnotationMetadataReadingVisitor visitor = new AnnotationMetadataReadingVisitor(classLoader);
		classReader.accept(visitor, ClassReader.SKIP_DEBUG);
		this.annotationMetadata = visitor;
		// (since AnnotationMetadataReadingVisitor extends ClassMetadataReadingVisitor)
		this.classMetadata = visitor;
		this.resource = resource;
	}

	@Override
	public Resource getResource() {
		return this.resource;
	}

	@Override
	public ClassMetadata getClassMetadata() {
		return this.classMetadata;
	}

	@Override
	public AnnotationMetadata getAnnotationMetadata() {
		return this.annotationMetadata;
	}
}
