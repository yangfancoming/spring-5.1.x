

package org.springframework.jmx.export.naming;


public class KeyNamingStrategyTests extends AbstractNamingStrategyTests {

	private static final String OBJECT_NAME = "spring:name=test";


	@Override
	protected ObjectNamingStrategy getStrategy() throws Exception {
		return new KeyNamingStrategy();
	}

	@Override
	protected Object getManagedResource() {
		return new Object();
	}

	@Override
	protected String getKey() {
		return OBJECT_NAME;
	}

	@Override
	protected String getCorrectObjectName() {
		return OBJECT_NAME;
	}

}
