

package org.springframework.aop.config;

import org.junit.Before;
import org.junit.Test;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.util.ClassUtils;
import org.springframework.util.SerializationTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.XmlWebApplicationContext;

import static java.lang.String.format;
import static org.junit.Assert.*;

/**
 * Integration tests for scoped proxy use in conjunction with aop: namespace.
 * Deemed an integration test because .web mocks and application contexts are required.
 * @see org.springframework.aop.config.AopNamespaceHandlerTests
 */
public class AopNamespaceHandlerScopeIntegrationTests {

	private static final String CONTEXT = format("classpath:%s-context.xml",ClassUtils.convertClassNameToResourcePath(AopNamespaceHandlerScopeIntegrationTests.class.getName()));

	private ApplicationContext context;

	@Before
	public void setUp() {
		XmlWebApplicationContext wac = new XmlWebApplicationContext();
		wac.setConfigLocations(CONTEXT);
		wac.refresh();
		this.context = wac;
	}

	@Test
	public void testSingletonScoping() throws Exception {
		ITestBean scoped = (ITestBean) this.context.getBean("singletonScoped");
		assertTrue("Should be AOP proxy", AopUtils.isAopProxy(scoped));
		assertTrue("Should be target class proxy", scoped instanceof TestBean);
		String rob = "Rob Harrop";
		String bram = "Bram Smeets";
		assertEquals(rob, scoped.getName());
		scoped.setName(bram);
		assertEquals(bram, scoped.getName());
		ITestBean deserialized = (ITestBean) SerializationTestUtils.serializeAndDeserialize(scoped);
		assertEquals(bram, deserialized.getName());
	}

	@Test
	public void testRequestScoping() throws Exception {
		MockHttpServletRequest oldRequest = new MockHttpServletRequest();
		MockHttpServletRequest newRequest = new MockHttpServletRequest();

		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(oldRequest));

		ITestBean scoped = (ITestBean) this.context.getBean("requestScoped");
		assertTrue("Should be AOP proxy", AopUtils.isAopProxy(scoped));
		assertTrue("Should be target class proxy", scoped instanceof TestBean);

		ITestBean testBean = (ITestBean) this.context.getBean("testBean");
		assertTrue("Should be AOP proxy", AopUtils.isAopProxy(testBean));
		assertFalse("Regular bean should be JDK proxy", testBean instanceof TestBean);

		String rob = "Rob Harrop";
		String bram = "Bram Smeets";

		assertEquals(rob, scoped.getName());
		scoped.setName(bram);
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(newRequest));
		assertEquals(rob, scoped.getName());
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(oldRequest));
		assertEquals(bram, scoped.getName());

		assertTrue("Should have advisors", ((Advised) scoped).getAdvisors().length > 0);
	}

	@Test
	public void testSessionScoping() throws Exception {
		MockHttpSession oldSession = new MockHttpSession();
		MockHttpSession newSession = new MockHttpSession();

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setSession(oldSession);
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		ITestBean scoped = (ITestBean) this.context.getBean("sessionScoped");
		assertTrue("Should be AOP proxy", AopUtils.isAopProxy(scoped));
		assertFalse("Should not be target class proxy", scoped instanceof TestBean);

		ITestBean scopedAlias = (ITestBean) this.context.getBean("sessionScopedAlias");
		assertSame(scoped, scopedAlias);

		ITestBean testBean = (ITestBean) this.context.getBean("testBean");
		assertTrue("Should be AOP proxy", AopUtils.isAopProxy(testBean));
		assertFalse("Regular bean should be JDK proxy", testBean instanceof TestBean);

		String rob = "Rob Harrop";
		String bram = "Bram Smeets";

		assertEquals(rob, scoped.getName());
		scoped.setName(bram);
		request.setSession(newSession);
		assertEquals(rob, scoped.getName());
		request.setSession(oldSession);
		assertEquals(bram, scoped.getName());

		assertTrue("Should have advisors", ((Advised) scoped).getAdvisors().length > 0);
	}

}
