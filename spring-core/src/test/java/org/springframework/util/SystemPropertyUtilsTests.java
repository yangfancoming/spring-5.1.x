

package org.springframework.util;

import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.*;


public class SystemPropertyUtilsTests {

	@Test
	public void testReplaceFromSystemProperty() {
		System.setProperty("test.prop", "bar");
		try {
			String resolved = SystemPropertyUtils.resolvePlaceholders("${test.prop}");
			assertEquals("bar", resolved);
		}
		finally {
			System.getProperties().remove("test.prop");
		}
	}

	@Test
	public void testReplaceFromSystemPropertyWithDefault() {
		System.setProperty("test.prop", "bar");
		try {
			String resolved = SystemPropertyUtils.resolvePlaceholders("${test.prop:foo}");
			assertEquals("bar", resolved);
		}
		finally {
			System.getProperties().remove("test.prop");
		}
	}

	@Test
	public void testReplaceFromSystemPropertyWithExpressionDefault() {
		System.setProperty("test.prop", "bar");
		try {
			String resolved = SystemPropertyUtils.resolvePlaceholders("${test.prop:#{foo.bar}}");
			assertEquals("bar", resolved);
		}
		finally {
			System.getProperties().remove("test.prop");
		}
	}

	@Test
	public void testReplaceFromSystemPropertyWithExpressionContainingDefault() {
		System.setProperty("test.prop", "bar");
		try {
			String resolved = SystemPropertyUtils.resolvePlaceholders("${test.prop:Y#{foo.bar}X}");
			assertEquals("bar", resolved);
		}
		finally {
			System.getProperties().remove("test.prop");
		}
	}

	@Test
	public void testReplaceWithDefault() {
		String resolved = SystemPropertyUtils.resolvePlaceholders("${test.prop:foo}");
		assertEquals("foo", resolved);
	}

	@Test
	public void testReplaceWithExpressionDefault() {
		String resolved = SystemPropertyUtils.resolvePlaceholders("${test.prop:#{foo.bar}}");
		assertEquals("#{foo.bar}", resolved);
	}

	@Test
	public void testReplaceWithExpressionContainingDefault() {
		String resolved = SystemPropertyUtils.resolvePlaceholders("${test.prop:Y#{foo.bar}X}");
		assertEquals("Y#{foo.bar}X", resolved);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testReplaceWithNoDefault() {
		String resolved = SystemPropertyUtils.resolvePlaceholders("${test.prop}");
		assertEquals("", resolved);
	}

	@Test
	public void testReplaceWithNoDefaultIgnored() {
		String resolved = SystemPropertyUtils.resolvePlaceholders("${test.prop}", true);
		assertEquals("${test.prop}", resolved);
	}

	@Test
	public void testReplaceWithEmptyDefault() {
		String resolved = SystemPropertyUtils.resolvePlaceholders("${test.prop:}");
		assertEquals("", resolved);
	}

	@Test
	public void testRecursiveFromSystemProperty() {
		System.setProperty("test.prop", "foo=${bar}");
		System.setProperty("bar", "baz");
		try {
			String resolved = SystemPropertyUtils.resolvePlaceholders("${test.prop}");
			assertEquals("foo=baz", resolved);
		}
		finally {
			System.getProperties().remove("test.prop");
			System.getProperties().remove("bar");
		}
	}

	@Test
	public void testReplaceFromEnv() {
		Map<String,String> env = System.getenv();
		if (env.containsKey("PATH")) {
			String text = "${PATH}";
			assertEquals(env.get("PATH"), SystemPropertyUtils.resolvePlaceholders(text));
		}
	}

}
