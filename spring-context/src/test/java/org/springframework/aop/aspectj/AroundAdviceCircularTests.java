

package org.springframework.aop.aspectj;

import org.junit.Test;

import org.springframework.aop.support.AopUtils;

import static org.junit.Assert.*;


public class AroundAdviceCircularTests extends AroundAdviceBindingTests {

	@Test
	public void testBothBeansAreProxies() {
		Object tb = ctx.getBean("testBean");
		assertTrue(AopUtils.isAopProxy(tb));
		Object tb2 = ctx.getBean("testBean2");
		assertTrue(AopUtils.isAopProxy(tb2));
	}

}
