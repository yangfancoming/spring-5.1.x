

package org.springframework.core;

import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.*;


public class AttributeAccessorSupportTests {

	private static final String KEY = "foo";
	private static final String VALUE = "bar";

	private AttributeAccessor attributeAccessor = new SimpleAttributeAccessorSupport();


	@Test
	public void setAndGet()  {
		assertNull(attributeAccessor.getAttribute(KEY));
		attributeAccessor.setAttribute(KEY, VALUE);
		assertEquals(VALUE, attributeAccessor.getAttribute(KEY));
	}

	@Test
	public void setAndHas()  {
		assertFalse(attributeAccessor.hasAttribute(KEY));
		attributeAccessor.setAttribute(KEY, VALUE);
		assertTrue(attributeAccessor.hasAttribute(KEY));
	}

	@Test
	public void remove()  {
		assertFalse(attributeAccessor.hasAttribute(KEY));
		attributeAccessor.setAttribute(KEY, VALUE);
		assertEquals(VALUE, attributeAccessor.removeAttribute(KEY));
		assertFalse(attributeAccessor.hasAttribute(KEY));
	}

	@Test
	public void attributeNames()  {
		attributeAccessor.setAttribute(KEY, VALUE);
		attributeAccessor.setAttribute("abc", "123");
		String[] attributeNames = attributeAccessor.attributeNames();
		Arrays.sort(attributeNames);
		assertTrue(Arrays.binarySearch(attributeNames, KEY) > -1);
		assertTrue(Arrays.binarySearch(attributeNames, "abc") > -1);
	}

	@SuppressWarnings("serial")
	private static class SimpleAttributeAccessorSupport extends AttributeAccessorSupport {
	}

}
