

package org.springframework.jmx.export;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.junit.Test;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jmx.support.ObjectNameManager;

import static org.junit.Assert.*;


public class LazyInitMBeanTests {

	@Test
	public void invokeOnLazyInitBean() throws Exception {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("org/springframework/jmx/export/lazyInit.xml");
		assertFalse(ctx.getBeanFactory().containsSingleton("testBean"));
		assertFalse(ctx.getBeanFactory().containsSingleton("testBean2"));
		try {
			MBeanServer server = (MBeanServer) ctx.getBean("server");
			ObjectName oname = ObjectNameManager.getInstance("bean:name=testBean2");
			String name = (String) server.getAttribute(oname, "Name");
			assertEquals("Invalid name returned", "foo", name);
		}
		finally {
			ctx.close();
		}
	}

}
