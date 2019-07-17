

package org.springframework.aop.aspectj;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Before;
import org.junit.Test;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.tests.sample.beans.ITestBean;

import static org.junit.Assert.*;


public class AspectJExpressionPointcutAdvisorTests {

	private ITestBean testBean;

	private CallCountingInterceptor interceptor;


	@Before
	public void setup() {
		ClassPathXmlApplicationContext ctx =
				new ClassPathXmlApplicationContext(getClass().getSimpleName() + ".xml", getClass());
		testBean = (ITestBean) ctx.getBean("testBean");
		interceptor = (CallCountingInterceptor) ctx.getBean("interceptor");
	}


	@Test
	public void testPointcutting() {
		assertEquals("Count should be 0", 0, interceptor.getCount());
		testBean.getSpouses();
		assertEquals("Count should be 1", 1, interceptor.getCount());
		testBean.getSpouse();
		assertEquals("Count should be 1", 1, interceptor.getCount());
	}

}


class CallCountingInterceptor implements MethodInterceptor {

	private int count;

	@Override
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		count++;
		return methodInvocation.proceed();
	}

	public int getCount() {
		return count;
	}

	public void reset() {
		this.count = 0;
	}
}
