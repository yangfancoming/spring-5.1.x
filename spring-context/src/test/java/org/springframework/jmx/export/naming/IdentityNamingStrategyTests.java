

package org.springframework.jmx.export.naming;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.junit.Test;

import org.springframework.jmx.JmxTestBean;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import static org.junit.Assert.*;


public class IdentityNamingStrategyTests {

	@Test
	public void naming() throws MalformedObjectNameException {
		JmxTestBean bean = new JmxTestBean();
		IdentityNamingStrategy strategy = new IdentityNamingStrategy();
		ObjectName objectName = strategy.getObjectName(bean, "null");
		assertEquals("Domain is incorrect", bean.getClass().getPackage().getName(),
				objectName.getDomain());
		assertEquals("Type property is incorrect", ClassUtils.getShortName(bean.getClass()),
				objectName.getKeyProperty(IdentityNamingStrategy.TYPE_KEY));
		assertEquals("HashCode property is incorrect", ObjectUtils.getIdentityHexString(bean),
				objectName.getKeyProperty(IdentityNamingStrategy.HASH_CODE_KEY));
	}

}
