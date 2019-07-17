

package org.springframework.beans.factory.parsing;

import org.junit.Test;

/**
 * Unit tests for {@link PropertyEntry}.
 *
 * @author Rick Evans

 */
public class PropertyEntryTests {

	@Test(expected = IllegalArgumentException.class)
	public void testCtorBailsOnNullPropertyNameArgument() throws Exception {
		new PropertyEntry(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCtorBailsOnEmptyPropertyNameArgument() throws Exception {
		new PropertyEntry("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCtorBailsOnWhitespacedPropertyNameArgument() throws Exception {
		new PropertyEntry("\t   ");
	}

}
