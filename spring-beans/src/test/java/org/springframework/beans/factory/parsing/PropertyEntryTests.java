

package org.springframework.beans.factory.parsing;

import org.junit.Test;

/**
 * Unit tests for {@link PropertyEntry}.
 */
public class PropertyEntryTests {

	@Test(expected = IllegalArgumentException.class)
	public void testCtorBailsOnNullPropertyNameArgument()  {
		new PropertyEntry(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCtorBailsOnEmptyPropertyNameArgument()  {
		new PropertyEntry("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCtorBailsOnWhitespacedPropertyNameArgument()  {
		new PropertyEntry("\t   ");
	}

}
