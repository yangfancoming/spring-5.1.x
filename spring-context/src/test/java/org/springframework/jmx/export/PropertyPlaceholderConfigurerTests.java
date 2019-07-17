

package org.springframework.jmx.export;

import javax.management.ObjectName;

import org.junit.Test;

import org.springframework.jmx.AbstractJmxTests;
import org.springframework.jmx.IJmxTestBean;

import static org.junit.Assert.*;

/**
 * @author Rob Harrop

 */
public class PropertyPlaceholderConfigurerTests extends AbstractJmxTests {

	@Override
	protected String getApplicationContextPath() {
		return "org/springframework/jmx/export/propertyPlaceholderConfigurer.xml";
	}

	@Test
	public void testPropertiesReplaced() {
		IJmxTestBean bean = (IJmxTestBean) getContext().getBean("testBean");

		assertEquals("Name is incorrect", "Rob Harrop", bean.getName());
		assertEquals("Age is incorrect", 100, bean.getAge());
	}

	@Test
	public void testPropertiesCorrectInJmx() throws Exception {
		ObjectName oname = new ObjectName("bean:name=proxyTestBean1");
		Object name = getServer().getAttribute(oname, "Name");
		Integer age = (Integer) getServer().getAttribute(oname, "Age");

		assertEquals("Name is incorrect in JMX", "Rob Harrop", name);
		assertEquals("Age is incorrect in JMX", 100, age.intValue());
	}

}

