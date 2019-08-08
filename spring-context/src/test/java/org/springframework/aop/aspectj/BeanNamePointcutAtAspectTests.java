

package org.springframework.aop.aspectj;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.junit.Test;

import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.TestBean;

import static org.junit.Assert.*;

/**
 * Test for correct application of the bean() PCD for &#64;AspectJ-based aspects.
 */
public class BeanNamePointcutAtAspectTests {

	private ITestBean testBean1;

	private ITestBean testBean3;

	private CounterAspect counterAspect;


	@org.junit.Before
	public void setup() {
		String simpleName = getClass().getSimpleName(); // "BeanNamePointcutAtAspectTests"
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(simpleName + ".xml", getClass());
		counterAspect = (CounterAspect) ctx.getBean("counterAspect");
		testBean1 = (ITestBean) ctx.getBean("testBean1");
		testBean3 = (ITestBean) ctx.getBean("testBean3");
	}


	@Test
	public void testMatchingBeanName() {
		assertTrue("Expected a proxy", testBean1 instanceof Advised);

		// Call two methods to test for SPR-3953-like condition
		testBean1.setAge(20);
		testBean1.setName("");
		assertEquals(2, counterAspect.count);
	}

	@Test
	public void testNonMatchingBeanName() {
		assertFalse("Didn't expect a proxy", testBean3 instanceof Advised);

		testBean3.setAge(20);
		assertEquals(0, counterAspect.count);
	}

	@Test
	public void testProgrammaticProxyCreation() {
		ITestBean testBean = new TestBean();

		AspectJProxyFactory factory = new AspectJProxyFactory();
		factory.setTarget(testBean);

		CounterAspect myCounterAspect = new CounterAspect();
		factory.addAspect(myCounterAspect);

		ITestBean proxyTestBean = factory.getProxy();

		assertTrue("Expected a proxy", proxyTestBean instanceof Advised);
		proxyTestBean.setAge(20);
		assertEquals("Programmatically created proxy shouldn't match bean()", 0, myCounterAspect.count);
	}

}


@Aspect
class CounterAspect {

	int count;

	@Before("execution(* set*(..)) && bean(testBean1)")
	public void increment1ForAnonymousPointcut() {
		count++;
	}

}
