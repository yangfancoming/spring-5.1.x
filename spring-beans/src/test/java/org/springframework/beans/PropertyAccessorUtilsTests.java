package org.springframework.beans;
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;


public class PropertyAccessorUtilsTests {

	/**
	 * @see PropertyAccessorUtils#canonicalPropertyName(java.lang.String)
	*/
	@Test
	public void testCanonicalPropertyName() {
		assertEquals("map", PropertyAccessorUtils.canonicalPropertyName("map"));
		assertEquals("map[key1]", PropertyAccessorUtils.canonicalPropertyName("map[key1]"));
		// 删除[]中的成对 单引号
		assertEquals("map[key1]", PropertyAccessorUtils.canonicalPropertyName("map['key1']"));
		// 删除[]中的成对 双引号
		assertEquals("map[key1]", PropertyAccessorUtils.canonicalPropertyName("map[\"key1\"]"));
		assertEquals("map[key1][key2]", PropertyAccessorUtils.canonicalPropertyName("map[key1][key2]"));
		// 删除连续[]中的成对 双引号和双引号
		assertEquals("map[key1][key2]", PropertyAccessorUtils.canonicalPropertyName("map['key1'][\"key2\"]"));
		assertEquals("map[key1].name", PropertyAccessorUtils.canonicalPropertyName("map[key1].name"));
		assertEquals("map[key1].name", PropertyAccessorUtils.canonicalPropertyName("map['key1'].name"));
		assertEquals("map[key1].name", PropertyAccessorUtils.canonicalPropertyName("map[\"key1\"].name"));

		// 只处理[]符号   其他符号无效
		assertEquals("('key')", PropertyAccessorUtils.canonicalPropertyName("('key')"));
		assertEquals("[key]", PropertyAccessorUtils.canonicalPropertyName("['key']"));
		// 只处理 单引号和双引号  其他符号不会处理
		assertEquals("[*key*]", PropertyAccessorUtils.canonicalPropertyName("[*key*]"));
		// 只处理 成对的单引号和双引号   单个不会处理
		assertEquals("['key]", PropertyAccessorUtils.canonicalPropertyName("['key]"));
	}

	@Test
	public void testCanonicalPropertyNames() {
		String[] original = new String[] {"map", "map[key1]", "map['key1']", "map[\"key1\"]", "map[key1][key2]","map['key1'][\"key2\"]", "map[key1].name", "map['key1'].name", "map[\"key1\"].name"};
		String[] canonical = new String[] {"map", "map[key1]", "map[key1]", "map[key1]", "map[key1][key2]","map[key1][key2]", "map[key1].name", "map[key1].name", "map[key1].name"};
		assertTrue(Arrays.equals(canonical, PropertyAccessorUtils.canonicalPropertyNames(original)));
	}



}
