

package org.springframework.core.io.support;

import java.nio.charset.Charset;

import org.junit.Test;
import org.springframework.core.io.DescriptiveResource;
import org.springframework.core.io.Resource;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link EncodedResource}.
 *
 * @author Sam Brannen
 * @since 3.2.14
 */
public class EncodedResourceTests {

	private static final String UTF8 = "UTF-8";
	private static final String UTF16 = "UTF-16";
	private static final Charset UTF8_CS = Charset.forName(UTF8);
	private static final Charset UTF16_CS = Charset.forName(UTF16);

	private final Resource resource = new DescriptiveResource("test");


	@Test
	public void equalsWithNullOtherObject() {
		assertFalse(new EncodedResource(resource).equals(null));
	}

	@Test
	public void equalsWithSameEncoding() {
		EncodedResource er1 = new EncodedResource(resource, UTF8);
		EncodedResource er2 = new EncodedResource(resource, UTF8);
		assertEquals(er1, er2);
	}

	@Test
	public void equalsWithDifferentEncoding() {
		EncodedResource er1 = new EncodedResource(resource, UTF8);
		EncodedResource er2 = new EncodedResource(resource, UTF16);
		assertNotEquals(er1, er2);
	}

	@Test
	public void equalsWithSameCharset() {
		EncodedResource er1 = new EncodedResource(resource, UTF8_CS);
		EncodedResource er2 = new EncodedResource(resource, UTF8_CS);
		assertEquals(er1, er2);
	}

	@Test
	public void equalsWithDifferentCharset() {
		EncodedResource er1 = new EncodedResource(resource, UTF8_CS);
		EncodedResource er2 = new EncodedResource(resource, UTF16_CS);
		assertNotEquals(er1, er2);
	}

	@Test
	public void equalsWithEncodingAndCharset() {
		EncodedResource er1 = new EncodedResource(resource, UTF8);
		EncodedResource er2 = new EncodedResource(resource, UTF8_CS);
		assertNotEquals(er1, er2);
	}

}
