

package org.springframework.aop.aspectj.autoproxy;

import org.junit.Before;
import org.junit.Test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.*;


public class AnnotationBindingTests {

	private AnnotatedTestBean testBean;


	@Before
	public void setup() {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(getClass().getSimpleName() + "-context.xml", getClass());
		testBean = (AnnotatedTestBean) ctx.getBean("testBean");
	}

	@Test
	public void testAnnotationBindingInAroundAdvice() {
		assertEquals("this value", testBean.doThis());
		assertEquals("that value", testBean.doThat());
	}

	@Test
	public void testNoMatchingWithoutAnnotationPresent() {
		assertEquals("doTheOther", testBean.doTheOther());
	}
}
