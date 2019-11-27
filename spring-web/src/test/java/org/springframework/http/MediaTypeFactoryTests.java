

package org.springframework.http;

import org.junit.Test;

import org.springframework.core.io.Resource;

import static org.junit.Assert.*;


public class MediaTypeFactoryTests {

	@Test
	public void getMediaType() {
		assertEquals(MediaType.APPLICATION_XML, MediaTypeFactory.getMediaType("file.xml").get());
		assertEquals(MediaType.parseMediaType("application/javascript"), MediaTypeFactory.getMediaType("file.js").get());
		assertEquals(MediaType.parseMediaType("text/css"), MediaTypeFactory.getMediaType("file.css").get());
		assertFalse(MediaTypeFactory.getMediaType("file.foobar").isPresent());
	}

	@Test
	public void nullParameter() {
		assertFalse(MediaTypeFactory.getMediaType((String) null).isPresent());
		assertFalse(MediaTypeFactory.getMediaType((Resource) null).isPresent());
		assertTrue(MediaTypeFactory.getMediaTypes(null).isEmpty());
	}

}
