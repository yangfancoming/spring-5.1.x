

package org.springframework.web.accept;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import org.springframework.http.MediaType;
import org.springframework.web.context.request.NativeWebRequest;

import static org.junit.Assert.*;

/**
 * A test fixture with a test sub-class of AbstractMappingContentNegotiationStrategy.
 * @since 3.2
 */
public class MappingContentNegotiationStrategyTests {

	@Test
	public void resolveMediaTypes() throws Exception {
		Map<String, MediaType> mapping = Collections.singletonMap("json", MediaType.APPLICATION_JSON);
		TestMappingContentNegotiationStrategy strategy = new TestMappingContentNegotiationStrategy("json", mapping);

		List<MediaType> mediaTypes = strategy.resolveMediaTypes(null);

		assertEquals(1, mediaTypes.size());
		assertEquals("application/json", mediaTypes.get(0).toString());
	}

	@Test
	public void resolveMediaTypesNoMatch() throws Exception {
		Map<String, MediaType> mapping = null;
		TestMappingContentNegotiationStrategy strategy = new TestMappingContentNegotiationStrategy("blah", mapping);

		List<MediaType> mediaTypes = strategy.resolveMediaTypes(null);

		assertEquals(ContentNegotiationStrategy.MEDIA_TYPE_ALL_LIST, mediaTypes);
	}

	@Test
	public void resolveMediaTypesNoKey() throws Exception {
		Map<String, MediaType> mapping = Collections.singletonMap("json", MediaType.APPLICATION_JSON);
		TestMappingContentNegotiationStrategy strategy = new TestMappingContentNegotiationStrategy(null, mapping);

		List<MediaType> mediaTypes = strategy.resolveMediaTypes(null);

		assertEquals(ContentNegotiationStrategy.MEDIA_TYPE_ALL_LIST, mediaTypes);
	}

	@Test
	public void resolveMediaTypesHandleNoMatch() throws Exception {
		Map<String, MediaType> mapping = null;
		TestMappingContentNegotiationStrategy strategy = new TestMappingContentNegotiationStrategy("xml", mapping);

		List<MediaType> mediaTypes = strategy.resolveMediaTypes(null);

		assertEquals(1, mediaTypes.size());
		assertEquals("application/xml", mediaTypes.get(0).toString());
	}


	private static class TestMappingContentNegotiationStrategy extends AbstractMappingContentNegotiationStrategy {

		private final String extension;

		public TestMappingContentNegotiationStrategy(String extension, Map<String, MediaType> mapping) {
			super(mapping);
			this.extension = extension;
		}

		@Override
		protected String getMediaTypeKey(NativeWebRequest request) {
			return this.extension;
		}

		@Override
		protected MediaType handleNoMatch(NativeWebRequest request, String mappingKey) {
			return "xml".equals(mappingKey) ? MediaType.APPLICATION_XML : null;
		}
	}

}
