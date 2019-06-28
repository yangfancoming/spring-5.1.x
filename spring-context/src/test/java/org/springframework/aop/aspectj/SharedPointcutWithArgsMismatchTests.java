

package org.springframework.aop.aspectj;

import org.junit.Before;
import org.junit.Test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * See SPR-1682.
 *
 * @author Adrian Colyer
 * @author Chris Beams
 */
public class SharedPointcutWithArgsMismatchTests {

	private ToBeAdvised toBeAdvised;


	@Before
	public void setup() {
		ClassPathXmlApplicationContext ctx =
				new ClassPathXmlApplicationContext(getClass().getSimpleName() + ".xml", getClass());
		toBeAdvised = (ToBeAdvised) ctx.getBean("toBeAdvised");
	}


	@Test
	public void testMismatchedArgBinding() {
		this.toBeAdvised.foo("Hello");
	}

}


class ToBeAdvised {

	public void foo(String s) {
		System.out.println(s);
	}
}


class MyAspect {

	public void doBefore(int x) {
		System.out.println(x);
	}

	public void doBefore(String x) {
		System.out.println(x);
	}
}
