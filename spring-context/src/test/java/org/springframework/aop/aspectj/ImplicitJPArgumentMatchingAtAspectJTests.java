
package org.springframework.aop.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.Test;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.tests.sample.beans.TestBean;

/**
 * Tests to check if the first implicit join point argument is correctly processed.
 * See SPR-3723 for more details.
 *
 * @author Ramnivas Laddad

 */
public class ImplicitJPArgumentMatchingAtAspectJTests {

	@Test
	public void testAspect() {
		// nothing to really test; it is enough if we don't get error while creating app context
		new ClassPathXmlApplicationContext(getClass().getSimpleName() + ".xml", getClass());
	}

	@Aspect
	static class CounterAtAspectJAspect {
		@Around(value="execution(* org.springframework.tests.sample.beans.TestBean.*(..)) and this(bean) and args(argument)",
				argNames="bean,argument")
		public void increment(ProceedingJoinPoint pjp, TestBean bean, Object argument) throws Throwable {
			pjp.proceed();
		}
	}
}

