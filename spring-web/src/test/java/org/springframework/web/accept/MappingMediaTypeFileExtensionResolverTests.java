
package org.springframework.web.accept;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import org.springframework.http.MediaType;

import static org.junit.Assert.*;

/**
 * Test fixture for {@link MappingMediaTypeFileExtensionResolver}.
 *
 *
 * @author Melissa Hartsock
 */
public class MappingMediaTypeFileExtensionResolverTests {

	@Test
	public void resolveExtensions() {
		Map<String, MediaType> mapping = Collections.singletonMap("json", MediaType.APPLICATION_JSON);
		MappingMediaTypeFileExtensionResolver resolver = new MappingMediaTypeFileExtensionResolver(mapping);
		List<String> extensions = resolver.resolveFileExtensions(MediaType.APPLICATION_JSON);

		assertEquals(1, extensions.size());
		assertEquals("json", extensions.get(0));
	}

	@Test
	public void resolveExtensionsNoMatch() {
		Map<String, MediaType> mapping = Collections.singletonMap("json", MediaType.APPLICATION_JSON);
		MappingMediaTypeFileExtensionResolver resolver = new MappingMediaTypeFileExtensionResolver(mapping);
		List<String> extensions = resolver.resolveFileExtensions(MediaType.TEXT_HTML);

		assertTrue(extensions.isEmpty());
	}

	/**
	 * Unit test for SPR-13747 - ensures that reverse lookup of media type from media
	 * type key is case-insensitive.
	 */
	@Test
	public void lookupMediaTypeCaseInsensitive() {
		Map<String, MediaType> mapping = Collections.singletonMap("json", MediaType.APPLICATION_JSON);
		MappingMediaTypeFileExtensionResolver resolver = new MappingMediaTypeFileExtensionResolver(mapping);
		MediaType mediaType = resolver.lookupMediaType("JSON");

		assertEquals(MediaType.APPLICATION_JSON, mediaType);
	}

}
