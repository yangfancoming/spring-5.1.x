

package org.springframework.beans;

import org.junit.Test;

import org.springframework.tests.sample.beans.TestBean;

import static org.junit.Assert.*;

/**
 * Specific {@link DirectFieldAccessor} tests.
 */
public class DirectFieldAccessorTests extends AbstractPropertyAccessorTests {

	@Override
	protected DirectFieldAccessor createAccessor(Object target) {
		return new DirectFieldAccessor(target);
	}


	@Test
	public void withShadowedField() {
		final StringBuilder sb = new StringBuilder();

		@SuppressWarnings("serial")
		TestBean target = new TestBean() {
			@SuppressWarnings("unused")
			StringBuilder name = sb;
		};

		DirectFieldAccessor dfa = createAccessor(target);
		assertEquals(StringBuilder.class, dfa.getPropertyType("name"));
		assertEquals(sb, dfa.getPropertyValue("name"));
	}

}
