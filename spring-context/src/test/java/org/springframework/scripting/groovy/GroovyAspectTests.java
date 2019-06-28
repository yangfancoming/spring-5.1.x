

package org.springframework.scripting.groovy;

import org.junit.Test;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.util.ClassUtils;

import static org.junit.Assert.*;

/**
 * @author Dave Syer
 * @author Sam Brannen
 */
public class GroovyAspectTests {

	private final LogUserAdvice logAdvice = new LogUserAdvice();

	private final GroovyScriptFactory scriptFactory = new GroovyScriptFactory("GroovyServiceImpl.grv");


	@Test
	public void manualGroovyBeanWithUnconditionalPointcut() throws Exception {
		TestService target = (TestService) scriptFactory.getScriptedObject(new ResourceScriptSource(
				new ClassPathResource("GroovyServiceImpl.grv", getClass())));

		testAdvice(new DefaultPointcutAdvisor(logAdvice), logAdvice, target, "GroovyServiceImpl");
	}

	@Test
	public void manualGroovyBeanWithStaticPointcut() throws Exception {
		TestService target = (TestService) scriptFactory.getScriptedObject(new ResourceScriptSource(
				new ClassPathResource("GroovyServiceImpl.grv", getClass())));

		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression(String.format("execution(* %s.TestService+.*(..))", ClassUtils.getPackageName(getClass())));
		testAdvice(new DefaultPointcutAdvisor(pointcut, logAdvice), logAdvice, target, "GroovyServiceImpl", true);
	}

	@Test
	public void manualGroovyBeanWithDynamicPointcut() throws Exception {
		TestService target = (TestService) scriptFactory.getScriptedObject(new ResourceScriptSource(
				new ClassPathResource("GroovyServiceImpl.grv", getClass())));

		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression(String.format("@within(%s.Log)", ClassUtils.getPackageName(getClass())));
		testAdvice(new DefaultPointcutAdvisor(pointcut, logAdvice), logAdvice, target, "GroovyServiceImpl", false);
	}

	@Test
	public void manualGroovyBeanWithDynamicPointcutProxyTargetClass() throws Exception {
		TestService target = (TestService) scriptFactory.getScriptedObject(new ResourceScriptSource(
				new ClassPathResource("GroovyServiceImpl.grv", getClass())));

		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression(String.format("@within(%s.Log)", ClassUtils.getPackageName(getClass())));
		testAdvice(new DefaultPointcutAdvisor(pointcut, logAdvice), logAdvice, target, "GroovyServiceImpl", true);
	}

	private void testAdvice(Advisor advisor, LogUserAdvice logAdvice, TestService target, String message)
			throws Exception {

		testAdvice(advisor, logAdvice, target, message, false);
	}

	private void testAdvice(Advisor advisor, LogUserAdvice logAdvice, TestService target, String message,
			boolean proxyTargetClass) throws Exception {

		logAdvice.reset();

		ProxyFactory factory = new ProxyFactory(target);
		factory.setProxyTargetClass(proxyTargetClass);
		factory.addAdvisor(advisor);
		TestService bean = (TestService) factory.getProxy();

		assertEquals(0, logAdvice.getCountThrows());
		try {
			bean.sayHello();
			fail("Expected exception");
		}
		catch (TestException ex) {
			assertEquals(message, ex.getMessage());
		}
		assertEquals(1, logAdvice.getCountThrows());
	}

}
