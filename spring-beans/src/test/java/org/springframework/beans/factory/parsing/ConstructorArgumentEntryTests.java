

package org.springframework.beans.factory.parsing;

import org.junit.Test;

/**
 * Unit tests for {@link ConstructorArgumentEntry}.

 */
public class ConstructorArgumentEntryTests {

	@Test(expected = IllegalArgumentException.class)
	public void testCtorBailsOnNegativeCtorIndexArgument() {
		new ConstructorArgumentEntry(-1);
	}

}
