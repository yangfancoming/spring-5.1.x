

package org.springframework.core.type;

/**
 * @author Chris Beams
 * @since 3.1
 * @see AbstractClassMetadataMemberClassTests
 */
public class StandardClassMetadataMemberClassTests
		extends AbstractClassMetadataMemberClassTests {

	@Override
	public ClassMetadata getClassMetadataFor(Class<?> clazz) {
		return new StandardClassMetadata(clazz);
	}

}
