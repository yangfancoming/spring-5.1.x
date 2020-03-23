

package org.springframework.core;

import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.*;


public class AttributeAccessorSupportTests {

	private AttributeAccessor attributeAccessor = new SimpleAttributeAccessorSupport();
	private static final String KEY = "foo";
	private static final String VALUE = "bar";

	// 测试 get 功能
	@Test
	public void setAndGet()  {
		assertNull(attributeAccessor.getAttribute(KEY));
		attributeAccessor.setAttribute(KEY, VALUE);
		assertEquals(VALUE, attributeAccessor.getAttribute(KEY));
	}

	// 测试 has 功能
	@Test
	public void setAndHas()  {
		assertFalse(attributeAccessor.hasAttribute(KEY));
		attributeAccessor.setAttribute(KEY, VALUE);
		assertTrue(attributeAccessor.hasAttribute(KEY));
	}

	// 测试 remove 功能
	@Test
	public void remove()  {
		attributeAccessor.setAttribute(KEY, VALUE);
		assertEquals(VALUE, attributeAccessor.removeAttribute(KEY));
	}

	// 测试 attributeNames 功能
	@Test
	public void attributeNames()  {
		attributeAccessor.setAttribute(KEY, VALUE);
		attributeAccessor.setAttribute("abc", "123");
		String[] attributeNames = attributeAccessor.attributeNames();
		Arrays.sort(attributeNames);
		// 二进制算法查询 给定字符串是否在指定数组中
		assertTrue(Arrays.binarySearch(attributeNames, KEY) > -1);
		assertTrue(Arrays.binarySearch(attributeNames, "abc") > -1);
	}

	@SuppressWarnings("serial")
	private static class SimpleAttributeAccessorSupport extends AttributeAccessorSupport {
	}

}
