

package org.springframework.test.context.cache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.core.SpringProperties;

import static org.junit.Assert.*;
import static org.springframework.test.context.cache.ContextCacheUtils.*;
import static org.springframework.test.context.cache.ContextCache.*;

/**
 * Unit tests for {@link ContextCacheUtils}.
 *
 * @author Sam Brannen
 * @since 4.3
 */
public class ContextCacheUtilsTests {

	@Before
	@After
	public void clearProperties() {
		System.clearProperty(MAX_CONTEXT_CACHE_SIZE_PROPERTY_NAME);
		SpringProperties.setProperty(MAX_CONTEXT_CACHE_SIZE_PROPERTY_NAME, null);
	}

	@Test
	public void retrieveMaxCacheSizeFromDefault() {
		assertDefaultValue();
	}

	@Test
	public void retrieveMaxCacheSizeFromBogusSystemProperty() {
		System.setProperty(MAX_CONTEXT_CACHE_SIZE_PROPERTY_NAME, "bogus");
		assertDefaultValue();
	}

	@Test
	public void retrieveMaxCacheSizeFromBogusSpringProperty() {
		SpringProperties.setProperty(MAX_CONTEXT_CACHE_SIZE_PROPERTY_NAME, "bogus");
		assertDefaultValue();
	}

	@Test
	public void retrieveMaxCacheSizeFromDecimalSpringProperty() {
		SpringProperties.setProperty(MAX_CONTEXT_CACHE_SIZE_PROPERTY_NAME, "3.14");
		assertDefaultValue();
	}

	@Test
	public void retrieveMaxCacheSizeFromSystemProperty() {
		System.setProperty(MAX_CONTEXT_CACHE_SIZE_PROPERTY_NAME, "42");
		assertEquals(42, retrieveMaxCacheSize());
	}

	@Test
	public void retrieveMaxCacheSizeFromSystemPropertyContainingWhitespace() {
		System.setProperty(MAX_CONTEXT_CACHE_SIZE_PROPERTY_NAME, "42\t");
		assertEquals(42, retrieveMaxCacheSize());
	}

	@Test
	public void retrieveMaxCacheSizeFromSpringProperty() {
		SpringProperties.setProperty(MAX_CONTEXT_CACHE_SIZE_PROPERTY_NAME, "99");
		assertEquals(99, retrieveMaxCacheSize());
	}

	private static void assertDefaultValue() {
		assertEquals(DEFAULT_MAX_CONTEXT_CACHE_SIZE, retrieveMaxCacheSize());
	}

}
