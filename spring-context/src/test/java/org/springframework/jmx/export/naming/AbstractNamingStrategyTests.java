

package org.springframework.jmx.export.naming;

import javax.management.ObjectName;

import org.junit.Test;

import static org.junit.Assert.*;


public abstract class AbstractNamingStrategyTests {

	@Test
	public void naming() throws Exception {
		ObjectNamingStrategy strat = getStrategy();
		ObjectName objectName = strat.getObjectName(getManagedResource(), getKey());
		assertEquals(objectName.getCanonicalName(), getCorrectObjectName());
	}

	protected abstract ObjectNamingStrategy getStrategy() throws Exception;

	protected abstract Object getManagedResource() throws Exception;

	protected abstract String getKey();

	protected abstract String getCorrectObjectName();

}
