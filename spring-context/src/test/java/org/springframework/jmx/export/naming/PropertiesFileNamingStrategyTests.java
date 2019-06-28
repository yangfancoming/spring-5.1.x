

package org.springframework.jmx.export.naming;

import org.springframework.core.io.ClassPathResource;

/**
 * @author Juergen Hoeller
 */
public class PropertiesFileNamingStrategyTests extends PropertiesNamingStrategyTests {

	@Override
	protected ObjectNamingStrategy getStrategy() throws Exception {
		KeyNamingStrategy strat = new KeyNamingStrategy();
		strat.setMappingLocation(new ClassPathResource("jmx-names.properties", getClass()));
		strat.afterPropertiesSet();
		return strat;
	}

}
