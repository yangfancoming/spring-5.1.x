

package org.springframework.scripting.groovy;

import org.junit.After;
import org.junit.Test;

import org.springframework.context.support.GenericXmlApplicationContext;

import static org.junit.Assert.*;

/**
 * @author Dave Syer
 */
public class GroovyAspectIntegrationTests {

	private GenericXmlApplicationContext context;

	@Test
	public void testJavaBean() {
		context = new GenericXmlApplicationContext(getClass(), getClass().getSimpleName()+"-java-context.xml");
		TestService bean = context.getBean("javaBean", TestService.class);
		LogUserAdvice logAdvice = context.getBean(LogUserAdvice.class);

		assertEquals(0, logAdvice.getCountThrows());
		try {
			bean.sayHello();
			fail("Expected exception");
		}
		catch (RuntimeException ex) {
			assertEquals("TestServiceImpl", ex.getMessage());
		}
		assertEquals(1, logAdvice.getCountThrows());

	}

	@Test
	public void testGroovyBeanInterface() {
		context = new GenericXmlApplicationContext(getClass(), getClass().getSimpleName()+"-groovy-interface-context.xml");
		TestService bean = context.getBean("groovyBean", TestService.class);
		LogUserAdvice logAdvice = context.getBean(LogUserAdvice.class);

		assertEquals(0, logAdvice.getCountThrows());
		try {
			bean.sayHello();
			fail("Expected exception");
		}
		catch (RuntimeException ex) {
			assertEquals("GroovyServiceImpl", ex.getMessage());
		}
		assertEquals(1, logAdvice.getCountThrows());
	}


	@Test
	public void testGroovyBeanDynamic() {
		context = new GenericXmlApplicationContext(getClass(), getClass().getSimpleName()+"-groovy-dynamic-context.xml");
		TestService bean = context.getBean("groovyBean", TestService.class);
		LogUserAdvice logAdvice = context.getBean(LogUserAdvice.class);

		assertEquals(0, logAdvice.getCountThrows());
		try {
			bean.sayHello();
			fail("Expected exception");
		}
		catch (RuntimeException ex) {
			assertEquals("GroovyServiceImpl", ex.getMessage());
		}
		// No proxy here because the pointcut only applies to the concrete class, not the interface
		assertEquals(0, logAdvice.getCountThrows());
		assertEquals(0, logAdvice.getCountBefore());
	}

	@Test
	public void testGroovyBeanProxyTargetClass() {
		context = new GenericXmlApplicationContext(getClass(), getClass().getSimpleName()+"-groovy-proxy-target-class-context.xml");
		TestService bean = context.getBean("groovyBean", TestService.class);
		LogUserAdvice logAdvice = context.getBean(LogUserAdvice.class);

		assertEquals(0, logAdvice.getCountThrows());
		try {
			bean.sayHello();
			fail("Expected exception");
		}
		catch (TestException ex) {
			assertEquals("GroovyServiceImpl", ex.getMessage());
		}
		assertEquals(1, logAdvice.getCountBefore());
		assertEquals(1, logAdvice.getCountThrows());
	}

	@After
	public void close() {
		if (context != null) {
			context.close();
		}
	}

}
