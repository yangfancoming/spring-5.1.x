

package org.springframework.aop.aspectj.autoproxy;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Test;

import org.springframework.aop.framework.Advised;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.tests.sample.beans.ITestBean;

import static org.junit.Assert.*;

/**
 * Test for ensuring the aspects aren't advised. See SPR-3893 for more details.
 *
 * @author Ramnivas Laddad

 */
public class AspectImplementingInterfaceTests {

	@Test
	public void testProxyCreation() {
		ClassPathXmlApplicationContext ctx =
			new ClassPathXmlApplicationContext(getClass().getSimpleName() + "-context.xml", getClass());

		ITestBean testBean = (ITestBean) ctx.getBean("testBean");
		AnInterface interfaceExtendingAspect = (AnInterface) ctx.getBean("interfaceExtendingAspect");

		assertTrue(testBean instanceof Advised);
		assertFalse(interfaceExtendingAspect instanceof Advised);
	}

}


interface AnInterface {
	public void interfaceMethod();
}


class InterfaceExtendingAspect implements AnInterface {
	public void increment(ProceedingJoinPoint pjp) throws Throwable {
		pjp.proceed();
	}

	@Override
	public void interfaceMethod() {
	}
}
