

package org.springframework.aop.config;

import org.junit.Test;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.tests.sample.beans.ITestBean;

import static org.junit.Assert.*;

/**
 * @author Rob Harrop

 */
public class AopNamespaceHandlerProxyTargetClassTests extends AopNamespaceHandlerTests {

	@Test
	public void testIsClassProxy() {
		ITestBean bean = getTestBean();
		assertTrue("Should be a CGLIB proxy", AopUtils.isCglibProxy(bean));
		assertTrue("Should expose proxy", ((Advised) bean).isExposeProxy());
	}

}
