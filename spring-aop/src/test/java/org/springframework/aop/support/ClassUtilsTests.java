
package org.springframework.aop.support;

import org.junit.Test;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.util.ClassUtils;

import static org.junit.Assert.*;


public class ClassUtilsTests {

	@Test
	public void getShortNameForCglibClass() {
		TestBean tb = new TestBean();
		ProxyFactory pf = new ProxyFactory();
		pf.setTarget(tb);
		pf.setProxyTargetClass(true);
		TestBean proxy = (TestBean) pf.getProxy();
		String className = ClassUtils.getShortName(proxy.getClass());
		assertEquals("Class name did not match", "TestBean", className);
	}
}
