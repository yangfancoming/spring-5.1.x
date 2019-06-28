

package org.springframework.jmx.export.naming;

import java.util.Properties;

/**
 * @author Rob Harrop
 * @author Juergen Hoeller
 */
public class PropertiesNamingStrategyTests extends AbstractNamingStrategyTests {

	private static final String OBJECT_NAME = "bean:name=namingTest";


	@Override
	protected ObjectNamingStrategy getStrategy() throws Exception {
		KeyNamingStrategy strat = new KeyNamingStrategy();
		Properties mappings = new Properties();
		mappings.setProperty("namingTest", "bean:name=namingTest");
		strat.setMappings(mappings);
		strat.afterPropertiesSet();
		return strat;
	}

	@Override
	protected Object getManagedResource() {
		return new Object();
	}

	@Override
	protected String getKey() {
		return "namingTest";
	}

	@Override
	protected String getCorrectObjectName() {
		return OBJECT_NAME;
	}

}
