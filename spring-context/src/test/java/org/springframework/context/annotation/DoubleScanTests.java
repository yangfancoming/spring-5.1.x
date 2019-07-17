

package org.springframework.context.annotation;


public class DoubleScanTests extends SimpleScanTests {

	@Override
	protected String[] getConfigLocations() {
		return new String[] {"doubleScanTests.xml"};
	}

}
