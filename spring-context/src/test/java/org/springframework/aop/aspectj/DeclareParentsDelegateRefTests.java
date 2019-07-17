

package org.springframework.aop.aspectj;

import org.junit.Before;
import org.junit.Test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.*;

/**
 * @author Ramnivas Laddad

 */
public class DeclareParentsDelegateRefTests {

	protected NoMethodsBean noMethodsBean;

	protected Counter counter;


	@Before
	public void setup() {
		ClassPathXmlApplicationContext ctx =
				new ClassPathXmlApplicationContext(getClass().getSimpleName() + ".xml", getClass());
		noMethodsBean = (NoMethodsBean) ctx.getBean("noMethodsBean");
		counter = (Counter) ctx.getBean("counter");
		counter.reset();
	}


	@Test
	public void testIntroductionWasMade() {
		assertTrue("Introduction must have been made", noMethodsBean instanceof ICounter);
	}

	@Test
	public void testIntroductionDelegation() {
		((ICounter)noMethodsBean).increment();
		assertEquals("Delegate's counter should be updated", 1, counter.getCount());
	}

}


interface NoMethodsBean {
}


class NoMethodsBeanImpl implements NoMethodsBean {
}

