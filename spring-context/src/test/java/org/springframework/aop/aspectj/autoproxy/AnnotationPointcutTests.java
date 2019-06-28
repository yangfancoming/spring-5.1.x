

package org.springframework.aop.aspectj.autoproxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Before;
import org.junit.Test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.*;

/**
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class AnnotationPointcutTests {

	private AnnotatedTestBean testBean;


	@Before
	public void setup() {
		ClassPathXmlApplicationContext ctx =
				new ClassPathXmlApplicationContext(getClass().getSimpleName() + "-context.xml", getClass());

		testBean = (AnnotatedTestBean) ctx.getBean("testBean");
	}


	@Test
	public void testAnnotationBindingInAroundAdvice() {
		assertEquals("this value", testBean.doThis());
	}

	@Test
	public void testNoMatchingWithoutAnnotationPresent() {
		assertEquals("doTheOther", testBean.doTheOther());
	}

}


class TestMethodInterceptor implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		return "this value";
	}
}
