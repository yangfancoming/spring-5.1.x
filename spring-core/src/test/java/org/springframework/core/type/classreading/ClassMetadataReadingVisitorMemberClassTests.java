

package org.springframework.core.type.classreading;

import java.io.IOException;

import org.springframework.core.type.AbstractClassMetadataMemberClassTests;
import org.springframework.core.type.ClassMetadata;

/**
 * @author Chris Beams
 * @since 3.1
 * @see AbstractClassMetadataMemberClassTests
 */
public class ClassMetadataReadingVisitorMemberClassTests
		extends AbstractClassMetadataMemberClassTests {

	@Override
	public ClassMetadata getClassMetadataFor(Class<?> clazz) {
		try {
			MetadataReader reader =
				new SimpleMetadataReaderFactory().getMetadataReader(clazz.getName());
			return reader.getAnnotationMetadata();
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

}
