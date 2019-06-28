

package org.springframework.aop.aspectj;

import org.junit.Before;
import org.junit.Test;
import test.mixin.Lockable;

import org.springframework.aop.support.AopUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.tests.sample.beans.ITestBean;

import static org.junit.Assert.*;

/**
 * @author Rod Johnson
 * @author Chris Beams
 */
public class DeclareParentsTests {

	private ITestBean testBeanProxy;

	private Object introductionObject;


	@Before
	public void setup() {
		ClassPathXmlApplicationContext ctx =
				new ClassPathXmlApplicationContext(getClass().getSimpleName() + ".xml", getClass());
		testBeanProxy = (ITestBean) ctx.getBean("testBean");
		introductionObject = ctx.getBean("introduction");
	}


	@Test
	public void testIntroductionWasMade() {
		assertTrue(AopUtils.isAopProxy(testBeanProxy));
		assertFalse("Introduction should not be proxied", AopUtils.isAopProxy(introductionObject));
		assertTrue("Introduction must have been made", testBeanProxy instanceof Lockable);
	}

	// TODO if you change type pattern from org.springframework.beans..*
	// to org.springframework..* it also matches introduction.
	// Perhaps generated advisor bean definition could be made to depend
	// on the introduction, in which case this would not be a problem.
	@Test
	public void testLockingWorks() {
		Lockable lockable = (Lockable) testBeanProxy;
		assertFalse(lockable.locked());

		// Invoke a non-advised method
		testBeanProxy.getAge();

		testBeanProxy.setName("");
		lockable.lock();
		try {
			testBeanProxy.setName(" ");
			fail("Should be locked");
		}
		catch (IllegalStateException ex) {
			// expected
		}
	}

}


class NonAnnotatedMakeLockable {

	public void checkNotLocked(Lockable mixin) {
		if (mixin.locked()) {
			throw new IllegalStateException("locked");
		}
	}
}
