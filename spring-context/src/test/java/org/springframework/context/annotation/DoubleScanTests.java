

package org.springframework.context.annotation;

/**
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class DoubleScanTests extends SimpleScanTests {

	@Override
	protected String[] getConfigLocations() {
		return new String[] {"doubleScanTests.xml"};
	}

}
