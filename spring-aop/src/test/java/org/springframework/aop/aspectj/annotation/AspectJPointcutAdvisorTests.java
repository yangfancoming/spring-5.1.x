

package org.springframework.aop.aspectj.annotation;

import org.junit.Test;
import test.aop.PerTargetAspect;

import org.springframework.aop.Pointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcutTests;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.tests.sample.beans.TestBean;

import static org.junit.Assert.*;


public class AspectJPointcutAdvisorTests {

	private final AspectJAdvisorFactory af = new ReflectiveAspectJAdvisorFactory();


	@Test
	public void testSingleton() throws SecurityException, NoSuchMethodException {
		AspectJExpressionPointcut ajexp = new AspectJExpressionPointcut();
		ajexp.setExpression(AspectJExpressionPointcutTests.MATCH_ALL_METHODS);

		InstantiationModelAwarePointcutAdvisorImpl ajpa = new InstantiationModelAwarePointcutAdvisorImpl(
				ajexp, TestBean.class.getMethod("getAge"), af,
				new SingletonMetadataAwareAspectInstanceFactory(new AbstractAspectJAdvisorFactoryTests.ExceptionAspect(null), "someBean"),
				1, "someBean");

		assertSame(Pointcut.TRUE, ajpa.getAspectMetadata().getPerClausePointcut());
		assertFalse(ajpa.isPerInstance());
	}

	@Test
	public void testPerTarget() throws SecurityException, NoSuchMethodException {
		AspectJExpressionPointcut ajexp = new AspectJExpressionPointcut();
		ajexp.setExpression(AspectJExpressionPointcutTests.MATCH_ALL_METHODS);

		InstantiationModelAwarePointcutAdvisorImpl ajpa = new InstantiationModelAwarePointcutAdvisorImpl(
				ajexp, TestBean.class.getMethod("getAge"), af,
				new SingletonMetadataAwareAspectInstanceFactory(new PerTargetAspect(), "someBean"),
				1, "someBean");

		assertNotSame(Pointcut.TRUE, ajpa.getAspectMetadata().getPerClausePointcut());
		assertTrue(ajpa.getAspectMetadata().getPerClausePointcut() instanceof AspectJExpressionPointcut);
		assertTrue(ajpa.isPerInstance());

		assertTrue(ajpa.getAspectMetadata().getPerClausePointcut().getClassFilter().matches(TestBean.class));
		assertFalse(ajpa.getAspectMetadata().getPerClausePointcut().getMethodMatcher().matches(
				TestBean.class.getMethod("getAge"), TestBean.class));

		assertTrue(ajpa.getAspectMetadata().getPerClausePointcut().getMethodMatcher().matches(
				TestBean.class.getMethod("getSpouse"), TestBean.class));
	}

	@Test(expected = AopConfigException.class)
	public void testPerCflowTarget() {
		testIllegalInstantiationModel(AbstractAspectJAdvisorFactoryTests.PerCflowAspect.class);
	}

	@Test(expected = AopConfigException.class)
	public void testPerCflowBelowTarget() {
		testIllegalInstantiationModel(AbstractAspectJAdvisorFactoryTests.PerCflowBelowAspect.class);
	}

	private void testIllegalInstantiationModel(Class<?> c) throws AopConfigException {
		new AspectMetadata(c, "someBean");
	}

}
