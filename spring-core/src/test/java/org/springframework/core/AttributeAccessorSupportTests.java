

package org.springframework.core;

import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Rob Harrop
 * @author Sam Brannen
 * @since 2.0
 */
public class AttributeAccessorSupportTests {

	private static final String NAME = "foo";

	private static final String VALUE = "bar";

	private AttributeAccessor attributeAccessor = new SimpleAttributeAccessorSupport();

	@Test
	public void setAndGet() throws Exception {
		this.attributeAccessor.setAttribute(NAME, VALUE);
		assertEquals(VALUE, this.attributeAccessor.getAttribute(NAME));
	}

	@Test
	public void setAndHas() throws Exception {
		assertFalse(this.attributeAccessor.hasAttribute(NAME));
		this.attributeAccessor.setAttribute(NAME, VALUE);
		assertTrue(this.attributeAccessor.hasAttribute(NAME));
	}

	@Test
	public void remove() throws Exception {
		assertFalse(this.attributeAccessor.hasAttribute(NAME));
		this.attributeAccessor.setAttribute(NAME, VALUE);
		assertEquals(VALUE, this.attributeAccessor.removeAttribute(NAME));
		assertFalse(this.attributeAccessor.hasAttribute(NAME));
	}

	@Test
	public void attributeNames() throws Exception {
		this.attributeAccessor.setAttribute(NAME, VALUE);
		this.attributeAccessor.setAttribute("abc", "123");
		String[] attributeNames = this.attributeAccessor.attributeNames();
		Arrays.sort(attributeNames);
		assertTrue(Arrays.binarySearch(attributeNames, NAME) > -1);
		assertTrue(Arrays.binarySearch(attributeNames, "abc") > -1);
	}

	@SuppressWarnings("serial")
	private static class SimpleAttributeAccessorSupport extends AttributeAccessorSupport {
	}

}
