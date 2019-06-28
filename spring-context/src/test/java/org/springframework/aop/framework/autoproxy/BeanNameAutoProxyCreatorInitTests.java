

package org.springframework.aop.framework.autoproxy;

import java.lang.reflect.Method;

import org.junit.Test;

import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.tests.sample.beans.TestBean;

import static org.junit.Assert.*;

/**
 * @author Juergen Hoeller
 * @author Dave Syer
 * @author Chris Beams
 */
public class BeanNameAutoProxyCreatorInitTests {

	@Test(expected = IllegalArgumentException.class)
	public void testIgnoreAdvisorThatIsCurrentlyInCreation() {
		ClassPathXmlApplicationContext ctx =
				new ClassPathXmlApplicationContext(getClass().getSimpleName() + "-context.xml", getClass());
		TestBean bean = (TestBean) ctx.getBean("bean");
		bean.setName("foo");
		assertEquals("foo", bean.getName());
		bean.setName(null);  // should throw
	}

}


class NullChecker implements MethodBeforeAdvice {

	@Override
	public void before(Method method, Object[] args, @Nullable Object target) throws Throwable {
		check(args);
	}

	private void check(Object[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i] == null) {
				throw new IllegalArgumentException("Null argument at position " + i);
			}
		}
	}

}
