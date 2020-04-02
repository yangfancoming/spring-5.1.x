

package org.springframework.aop.aspectj.generic;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.junit.Test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.*;

/**
 * Tests for AspectJ pointcut expression matching when working with bridge methods.
 *
 * Depending on the caller's static type either the bridge method or the user-implemented method
 * gets called as the way into the proxy. Therefore, we need tests for calling a bean with
 * static type set to type with generic method and to type with specific non-generic implementation.
 *
 * This class focuses on JDK proxy, while a subclass, GenericBridgeMethodMatchingClassProxyTests,
 * focuses on class proxying.
 *
 * See SPR-3556 for more details.
 *
 * @author Ramnivas Laddad

 */
public class GenericBridgeMethodMatchingTests {

	protected DerivedInterface<String> testBean;

	protected GenericCounterAspect counterAspect;


	@SuppressWarnings("unchecked")
	@org.junit.Before
	public void setup() {
		ClassPathXmlApplicationContext ctx =
				new ClassPathXmlApplicationContext(getClass().getSimpleName() + "-context.xml", getClass());

		counterAspect = (GenericCounterAspect) ctx.getBean("counterAspect");
		counterAspect.count = 0;

		testBean = (DerivedInterface<String>) ctx.getBean("testBean");
	}


	@Test
	public void testGenericDerivedInterfaceMethodThroughInterface() {
		testBean.genericDerivedInterfaceMethod("");
		assertEquals(1, counterAspect.count);
	}

	@Test
	public void testGenericBaseInterfaceMethodThroughInterface() {
		testBean.genericBaseInterfaceMethod("");
		assertEquals(1, counterAspect.count);
	}

}


interface BaseInterface<T> {

	void genericBaseInterfaceMethod(T t);
}


interface DerivedInterface<T> extends BaseInterface<T> {

	public void genericDerivedInterfaceMethod(T t);
}


class DerivedStringParameterizedClass implements DerivedInterface<String> {

	@Override
	public void genericDerivedInterfaceMethod(String t) {
	}

	@Override
	public void genericBaseInterfaceMethod(String t) {
	}
}

@Aspect
class GenericCounterAspect {

	public int count;

	@Before("execution(* *..BaseInterface+.*(..))")
	public void increment() {
		count++;
	}

}

